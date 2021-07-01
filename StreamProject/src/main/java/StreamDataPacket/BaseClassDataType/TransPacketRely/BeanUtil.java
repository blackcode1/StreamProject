package StreamDataPacket.BaseClassDataType.TransPacketRely;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import edu.thss.entity.RawDataPacket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BeanUtil {
	static Kryo kryo = new Kryo();

	public BeanUtil() {
	}

	public static synchronized byte[] toByteArray(Object obj) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Output output = new Output(bos, 40960);
		kryo.writeObject(output, obj);
		byte[] bytes = output.toBytes();
		output.close();
		return bytes;
	}

	public static synchronized <T> T toObject(byte[] bytes, Class<T> type) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		Input input = new Input(bis);
		T obj = kryo.readObject(input, type);
		input.close();
		return obj;
	}

	public static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);

		for(int i = 0; i < bArray.length; ++i) {
			String sTemp = Integer.toHexString(255 & bArray[i]);
			if (sTemp.length() < 2) {
				sb.append(0);
			}

			sb.append(sTemp.toUpperCase());
		}

		return sb.toString();
	}

	public static byte[] hexStringToByte(String hex) {
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();

		for(int i = 0; i < len; ++i) {
			int pos = i * 2;
			result[i] = (byte)(toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}

		return result;
	}

	public static byte[] LongToBytes(long data) {
		byte[] buffer = new byte[8];

		for(int i = 0; i < 8; ++i) {
			int offset = i * 8;
			buffer[i] = (byte)((int)(data >> offset & 255L));
		}

		return buffer;
	}

	public static long BytesToLong(byte[] bytes) {
		long values = 0L;

		for(int i = 0; i < 8; ++i) {
			values <<= 8;
			values |= (long)bytes[bytes.length - i - 1] & 255L;
		}

		return values;
	}

	public static byte[] getBytesForRaw(RawDataPacket packet) {
		try {
			byte[] ids = packet.getRawDataId().getBytes("UTF-8");
			byte[] topic = packet.getTopic().getBytes("UTF-8");
			byte[] sIp = packet.getSourceIp().getBytes("UTF-8");
			byte[] sPort = packet.getSourcePort().getBytes("UTF-8");
			byte[] ip = packet.getServerIp().getBytes("UTF-8");
			byte[] port = packet.getServerPort().getBytes("UTF-8");
			byte[] imei = packet.getIMEI().getBytes("UTF-8");
			byte[] time = LongToBytes(packet.getTimestamp());
			byte[] data = (byte[])((byte[])packet.getPacketData());
			byte[] split = new byte[]{13, 10};
			return concatAll(ids, split, topic, split, sIp, split, sPort, split, ip, split, port, split, imei, split, data, time);
		} catch (UnsupportedEncodingException var11) {
			var11.printStackTrace();
			return null;
		}
	}

	public static RawDataPacket split(byte[] bytes) {
		int length = bytes.length;
		List<Integer> splits = new ArrayList();
		splits.add(-2);

		for(int j = 0; j < length - 1; ++j) {
			if (bytes[j] == 13 && bytes[j + 1] == 10) {
				splits.add(j);
			}
		}

		while(splits.size() > 8) {
			splits.remove(splits.size() - 1);
		}

		splits.add(length - 8);
		List<byte[]> containers = new ArrayList();

		for(int i = 0; i < splits.size() - 1; ++i) {
			containers.add(Arrays.copyOfRange(bytes, (Integer)splits.get(i) + 2, (Integer)splits.get(i + 1)));
		}

		long timestamp = BytesToLong(Arrays.copyOfRange(bytes, length - 8, length));

		try {
			return new RawDataPacket(new String((byte[])containers.get(0), "UTF-8"), new String((byte[])containers.get(1), "UTF-8"), new String((byte[])containers.get(2), "UTF-8"), new String((byte[])containers.get(3), "UTF-8"), new String((byte[])containers.get(4), "UTF-8"), new String((byte[])containers.get(5), "UTF-8"), new String((byte[])containers.get(6), "UTF-8"), containers.get(7), timestamp);
		} catch (UnsupportedEncodingException var7) {
			var7.printStackTrace();
			return null;
		}
	}

	private static byte toByte(char c) {
		byte b = (byte)"0123456789ABCDEF".indexOf(c);
		return b;
	}

	private static byte[] concatAll(byte[] first, byte[]... rest) {
		int totalLength = first.length;
		byte[][] var3 = rest;
		int offset = rest.length;

		for(int var5 = 0; var5 < offset; ++var5) {
			byte[] array = var3[var5];
			totalLength += array.length;
		}

		byte[] result = Arrays.copyOf(first, totalLength);
		offset = first.length;
		byte[][] var10 = rest;
		int var11 = rest.length;

		for(int var7 = 0; var7 < var11; ++var7) {
			byte[] array = var10[var7];
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}

		return result;
	}

	public static void main(String[] args) {
		long value = (new Date()).getTime();
		RawDataPacket packet = new RawDataPacket("111", "topic", "10.1.1.1", "9000", "155.2.3.4", "9323", "111", hexStringToByte("7E0F862631039236145FD41065044000120302080D0A260E95010E0000023F7A0120055600010402A80000023F7E0200000102000003020000023F7F0120055600010404910000013F0F0F000000010300049C0000023F0402000001020000049F0000011F15E516001400000004BA0000023F040500029F00029F04F60000023F050120055600010406900000023F090200000102000006EA0000023F0A0120055600010408790000013F0E0F00000001030008840000023F0E0200000102000008870000011F15E416000000000008A20000023F0E0500029F00029F08DE0000023F0F012005560001040A780000023F13020000010200000AD20000023F14012005560001040C610000013F0F0F0000000103000C6C0000023F18020000010200000C6F0000011F15F11600000000000C8A0000023F180500029F00029F0CC60000023F19012005560001040E600000023F1D020000010200000EBA0000023F1E0120055600010410490000013F0E0F00000001030010540000023F220200000102000010570000011F15FE16000000000010720000023F220500029F00029F10AE0000023F230120055600010412480000023F270200000102000012A20000023F280120055600010414310000013F0F0F000000010300143C0000023F2C02000001020000143F0000011F15F1160000000000145A0000023F2C0500029F00029F14960000023F2D0120055600010416300000023F3102000001020000168A0000023F320120055600010418190000013F0F0F00000001030018240000023F360200000102000018280000011F161C16000000000018420000023F360500029F00029F187E0000023F37012005560001041A180000023F3B020000010200001A720000023F3C012005560001041C010000013F0F0F0000000103001C0C0000023F40020000010200001C100000011F160A1600000000001C2A0000023F400500029F00029F1C660000023F41012105560001041E000000023F45020000010200001E5A0000023F46012005560001041FE90000013F0F0F0000000103001FF40000023F4A020000010200001FF80000011F15D916001802000020120000023F4A0500029F00029F204E0000023F4B0120055600010421E80000023F4F0200000102000022420000023F500120055600010423D10000013F0E0F00000001030023DC0000023F540200000102000023E00000011F15DD16000000000023FA0000023F540500029F00029F24360000023F550121055600010425D00000023F5902000001020000262A0000023F5A0121055600010427B90000013F0F0F00000001030027C40000023F5E0200000102000027C80000011F160916000000000027E20000023F5E0500029F00029F281E0000023F5F0120055600010429B80000023F63020000010200002A120000023F64012005560001042BA10000013F0E0F0000000103002BAC0000023F68020000010200002BB00000011F161A1600000000002BCA0000023F680500029F00029F2C060000023F69012105560001042DA00000023F6D020000010200002DFA0000023F6E012105560001042F890000013F0F0F0000000103002F940000023F72020000010200002F980000011F15FF1600000000002FB20000023F720500029F00029F2FEE0000023F730121055600010431880000023F770200000102000031E20000023F780120055600010433710000013F0E0F000000010300337C0000023F7C0200000102000033800000011F15E9160000000000339A0000023F7C0500029F00029F33D60000023F7D0120055600010435700000023F020200000102000035CA0000023F030120055600010437590000013F0F0F00000001030037640000023F070200000101000037680000011F161316000000000037820000023F070500029F00029F37BE0000023F080120055600010439580000023F0C0200000101000039B20000023F0D012105560001043B420000013F0F0F000005550B013B4C0000023F11020000010100003B500000011F1B8F1603404400003B6B0000023F110500029F00029F3BA60000023F12012105560001003D400000023F16020000010100003D9A0000023F17012005560001003F2A0000013F0F10000006590B013F350000023F1B020000010100003F380000011F1F5F1601401700003F520000023F1B0500029F00029F3F8F0000023F1C012105560001003FE30000021F1A01023F2B302B233FED0000021F1A023C5500E600A6405B0000021F1B03FFFFFFFF000041290000023F200200000101000041830000023F210121055600010043120000013F10100000054B0B01431D0000023F250200000101000043200000011F20BB16012C230000433B0000023F250500029F00029F43770000023F260120055600010045110000023F2A02000001010000456B0000023F2B0120055600010046FA0000013F0E0C000008320F0447050000023F2F0200800101000047080000011F1EA61601D02A000047230000023F2F0500029F00029F475F0000023F300120055600010048F90000023F340204800101000049530000023F35012005560001004AE20000013F21230000010207004AED0000023F39020480010100004AF10000011F1EC216030C4E00004B0B0000023F390500029F00029F4B470000023F3A012005560001004BDCF8FF234A0000FFFFFFFFFFFF4CE10000023F3E020480010100004D3B0000023F3F012005560001004ECA0000013F16190000000107004ED50000023F43020480010100004ED90000011F1FA416029C4300004EF30000023F430500029F00029F4F2F0000023F440120055600010050C90000023F480204800101000051230000023F490120055600010052B20000013F210E0000042A0F0152BD0000023F4D0204800101000009007E"), value);
		System.out.println(bytesToHexString(getBytesForRaw(packet)));
		System.out.println(ObjectToString(split(getBytesForRaw(packet))));
		System.out.println(bytesToHexString((byte[])((byte[])split(getBytesForRaw(packet)).getPacketData())));
		System.out.println(value + " ," + BytesToLong(LongToBytes(value)));
	}

	public static String ObjectToString(Object obj) {
		StringBuffer result = new StringBuffer();
		Class cls = obj.getClass();
		result.append(cls.getName() + ":{");
		Field[] fields = cls.getDeclaredFields();

		for(int i = 0; i < fields.length; ++i) {
			Field f = fields[i];
			f.setAccessible(true);

			try {
				String out = String.format("%s : %s", f.getName(), f.get(obj));
				if (i < fields.length - 1) {
					out = out + ", ";
				}

				result.append(out);
			} catch (IllegalArgumentException var7) {
				var7.printStackTrace();
			} catch (IllegalAccessException var8) {
				var8.printStackTrace();
			}
		}

		result.append("}");
		return result.toString();
	}
}
