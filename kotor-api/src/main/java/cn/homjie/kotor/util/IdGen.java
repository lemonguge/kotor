package cn.homjie.kotor.util;

import java.util.UUID;

public class IdGen {

	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
