package net.jntsys;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;

public class XSLTResourceInjection implements ResourceLoaderAware {

	/* Для инициализации ResourceLoader возможно использовать два варианта:
	 * - использовать аннотацию @Autowired
	 * - реализовать в классе интерфейс ResourceLoaderAware определив setResourceLoader */
	//@Autowired
	private ResourceLoader resourceLoader;
	
	/* Согласно документации Spring по интерфейсу ResourceLoaderAware хорошей стратегией является использование DefaultResourceLoader
	 * для инициализации ResourceLoader-а по умолчанию, но при этом всё же использовать ResourceLoaderAware для обеспечения возможности
	 * переопределения при выполнении в ApplicationContext-окружении */
	public XSLTResourceInjection( ) {
		this.resourceLoader = new DefaultResourceLoader( );
	}
	
	public ResourceLoader getResourceLoader( ) { 
		return this.resourceLoader;
	}
	@Override
	public void setResourceLoader( ResourceLoader resourceLoader ) {
		this.resourceLoader = (resourceLoader != null ? resourceLoader : this.resourceLoader );
	}
	
	public Resource getResource( String res_name ) {
		return loadDynamicResource( );
	}
	

	private Resource loadDynamicResource( ) {
		genXSLTTemplate( );
		
		String contextPath = "/mnt/data/Develop/AppData/RES";
		//String viewFile = "test.xsl";
		String viewFile = "stHome.xslt";
        String viewFilePath = contextPath + File.separator + viewFile;
        
        try {
			Resource resource = new UrlResource("file://" + viewFilePath);
			System.out.println( "Ресурстный Bean: " + "file://" + viewFilePath);
			return resource;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void genXSLTTemplate( ) {   // Generate XSLT Template
		/* Cоздаём DOM, в котором собираем все компоненты stylesheet, при этом параметры компонентов хранятся в XML.
         * Фактически получается шаблон (набор правил) для сборки шаблонов (каркас страницы).
         * Формируемая DOM-модель имеет выходной метод - [xml], поскольку stylesheet по своей сути тоже XML.
         * Результирующий файл, передаваемый трансформеру для помещения результатов является темповым, как в доке o'reilly
         * и уже этот темповый файл передаётся в представление для объединения с данными.
         * Для статических страниц темповые файлы тоже статические и формируются один раз, в ресурсах, при запуске приложения
         * но при необходимости их возможно перегенерировать. 
         * На этапе реализации полнодинамического пользовательского интерфейса с шаблонизацией на стороне клиента, клиенту
         * будет передаваться шаблон каркаса, полученный в результате первой трансформации (набора правил) и XML файл с данными,
         * т.е. тот контент, который на данном этапе передаётся представлению [XsltView]. Вместо данного представления будет выбрано
         * то, которое просто передаёт клиенты два файла без совмещения, либо будет созданно собственное представление, наследующее
         * [XsltView] и реализующее собственно первую трансформацию (набо правил) для получения каркаса в формате stylesheet. При
         * этом необходимо будет также реализовать и класс наследующий XsltViewResolver как минимум для того чтобы передавать два
         * ресурса: первый - параметры каркаса для трансформации набора правил, второй - модель, передаваемая клиенту для совмещения.
         * Это будет правильнее !!! поскольку представление будет выпонять функцию предусматриваемую MVC, а не просто передавать
         * клиенту файл, т.е. отдельный класс отвечающий за преобразование набора правил и будет представлением */
 
        // открытие xsl:import и xsl:include осуществляется при помощи установки URIResolver для TransformerFactory.
        // в качестве URIResolver определяется собственный класс, реализующий данный интерфейс и он будет вызываться процессором при открытии необходимого включения.
        // этот класс возвращает не адрес, а уже конкретный ресурс передав классу его имя, а поскольку его реализация собственная, то получать можно хоть из базы.
        // В Spring-е URIResolver передаются через установку соответствующего параметра при настройке XsltViewResolver-а в конфигурации.
		
		// Процедуру трансформации оформить аналогично процедуре трансформации в XsltView, чтобы правильно выполнить все действия и организовать обработку исключений 
		
		String contextPath = "/mnt/data/Develop/AppData/RES";
		String assRuleSet = contextPath + File.separator + "test.xsl";	// Assembly Rule Set - набор правил сборки
		String assPValues = contextPath + File.separator + "citizens.xml";	// Assembly Parameter Values - значения параметров для сборки
		
		File xsltFile = new File( assRuleSet );
    	File xmlFile = new File( assPValues );
		
        try {   // добавление параметром при сборке
        	Source xsltSource = new StreamSource(xsltFile);
        	Source xmlSource = new StreamSource(xmlFile);
            Result result = new StreamResult(System.out);
            
            TransformerFactory transFact = TransformerFactory.newInstance( );

			Transformer trans = transFact.newTransformer(xsltSource);
			trans.setParameter("image_dir", "fuck out");
			trans.transform( xmlSource, result);

        } catch (Exception e1) {
			e1.printStackTrace();
		}


        try {   // трансформация в которой ресурс - тектовая строка
        	StringReader reader = new StringReader("<xml>blabla</xml>");
		    StringWriter writer = new StringWriter();
		    TransformerFactory tFactory = TransformerFactory.newInstance();
		    Transformer transformer = tFactory.newTransformer(
		            new StreamSource(xsltFile));

		    transformer.transform(
		            new StreamSource(reader),
		            new StreamResult(writer));

		    String result2 = writer.toString();
		    System.out.println( "Результат динамического преобразования: " + result2);
        
        } catch (Exception e1) {
			e1.printStackTrace();
        }
	}

}
