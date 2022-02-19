package com.mes.ncr.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

 

@ServletComponentScan
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) {
//		Map<String,Object> wA=new HashMap<>();
//		wA.put("CreateTime", "2020-07-11 22:00");
//		wA.put("SubmitTime", "2020-07-11 22:00");
//		@SuppressWarnings("unused")
//		BPMTaskBase wBPMTaskBase=CloneTool.Clone(wA, BPMTaskBase.class);
		//System.out.println(CloneTool.Clone("1", Integer.class));
		SpringApplication.run(Application.class, args);

	}

}
