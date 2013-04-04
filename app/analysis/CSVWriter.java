package analysis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//CSV file writer - see http://en.wikipedia.org/wiki/Comma-separated_values for the specs
//uses reflection
public class CSVWriter {

	public String separator = ",";
	public PrintWriter writer;
	public String path;

//	public class User {
//		
//		public User(int age, String name) {
//			this.age = age;
//			this.name = name;
//		}
//		
//		@CSV
//		int age;
//
//		@CSV
//		String name;
//	}

	public CSVWriter(String path) throws FileNotFoundException {
		this.path = path;
		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (UnsupportedEncodingException exception) {
			exception.printStackTrace();
		}
	}

	public CSVWriter(String path, String separator) throws FileNotFoundException, UnsupportedEncodingException {
		this(path);
		this.separator = separator;
	}


//	public static void main(String[] args) throws Exception {
//
//		CSVWriter test = new CSVWriter("/home/samuel/Desktop/test.txt", ";");
//
//		User paul = test.new User(21, "\"paul\"");
//		User pierre = test.new User(45, "pierrot");
//		List<User> users = new ArrayList<User>();
//		users.add(pierre);
//		users.add(paul);
//		test.write(users);
//	}

	public void write(List<?> list) throws IllegalArgumentException, IllegalAccessException {

		if(list.get(0) != null){
			Object type = list.get(0);			
			writeFieldNames(type);
		}
		
		for (Object object : list) {
			writeFieldValues(object);
		}

		writer.close();
	}

	private void writeFieldValues(Object object) throws IllegalArgumentException, IllegalAccessException {
		writeFieldProperty("value", object);
	}


	private void writeFieldNames(Object object) throws IllegalArgumentException, IllegalAccessException {
		writeFieldProperty("name", object);
	}

	private void writeFieldProperty(String method, Object object) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = object.getClass().getDeclaredFields();
		boolean isFirst = true;
		for (Field field : fields) {
			if(field.getAnnotation(CSV.class) != null){	
				if(!isFirst){
					writer.print(separator);
				}else{
					isFirst = false;
				}
				if(method == "name"){
					writer.print(field.getName());
				}else if(method == "value"){
					writer.print(field.get(object).toString());
				}
				
			}
		}
		writer.print("\n");
	}

}
