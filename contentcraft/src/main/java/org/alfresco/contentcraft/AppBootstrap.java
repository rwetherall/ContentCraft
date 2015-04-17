package org.alfresco.contentcraft;

import java.util.logging.Logger;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bootstraps a Spring app
 * @author Gethin James
 *
 */
public class AppBootstrap {

	private Logger logger;
    final ClassLoader appClassLoader = AppBootstrap.class.getClassLoader();
    
	public AppBootstrap(Logger logger) {
		super();
		this.logger = logger;
	}

	public void bootstrapSpring() {
        logger.info("Initializing Spring context.");
        
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml") {

            protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
                super.initBeanDefinitionReader(reader);
                reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
                reader.setBeanClassLoader(appClassLoader);
                setClassLoader(appClassLoader);                
            }
        };
       
        logger.info("..Initialized Spring context: "+ applicationContext.getBeanDefinitionCount() + " beans.");      
	}
}
