package sample.batch.step;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import sample.batch.exception.BatchSkipException;

@Component("itemReader")
public class EntryItemReader implements ItemReader<String> {

	private String[] input = {"Hello World", "hoge", "안녕하세요", null};

	private int index = 0;

	/**
	 * Reads next record from input
	 */
	public String read() throws Exception {

		String message = input[index++];

		if(message == null) {
			return null;
		}
		if (message.equals("hoge")) {
			throw new BatchSkipException("데이터 에러"
					+ message + "]");
		}
		return message;
	}
}
