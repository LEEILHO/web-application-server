package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	//BufferedReader 생성
        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
        	String line = br.readLine();
        	if(line == null) {
        		return;
        	}
        	log.debug(line);
        	String[] tokens = line.split(" ");
        	log.debug("test"+tokens[0]);
        	String url = tokens[1];
        	
        	if (url.startsWith("/user/create")) {
        		int index = url.indexOf("?");
        		String queryString = url.substring(index+1);
        		Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
        		User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        	}

        	while(!"".equals(line)) {
        		line = br.readLine();
        		log.debug(line);        		
        	}
        	
        	
        	
        	
            DataOutputStream dos = new DataOutputStream(out);
//            byte[] body = "Hello World".getBytes();
            byte[] body = Files.readAllBytes(new File("./webapp" + url ).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
