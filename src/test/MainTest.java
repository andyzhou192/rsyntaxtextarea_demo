package test;

import java.net.URLDecoder;

public class MainTest {

	public static void main (String[] args) throws java.lang.Exception
    {
        String url = "http://example.com/test?q=%.P%20some%20other%20Text";
        url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        System.out.println(url);
        System.out.println(URLDecoder.decode(url));
    }

}
