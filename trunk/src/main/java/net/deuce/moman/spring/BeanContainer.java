package net.deuce.moman.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanContainer {
	
	private static BeanContainer beanContainer = new BeanContainer();
	
	public static BeanContainer instance() { return beanContainer; }
	
	private ApplicationContext applicationContext;
	
	private BeanContainer() {
		try {
			applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@SuppressWarnings("unchecked")
	public Object getBean(String name, Class clazz) {
		return applicationContext.getBean(name, clazz);
	}

	public Object getBean(String name) {
		return applicationContext.getBean(name);
	}
}
