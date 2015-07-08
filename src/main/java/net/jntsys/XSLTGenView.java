package test;

import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.xslt.XsltView;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class XSLTGenView extends XsltView {
    @Override
    protected Source getStylesheetSource( ) {
        // получить значение динамического аттрибута также как получается Url в родительском классе
        System.out.println( "начало");

        String url = getUrl();
        if (logger.isDebugEnabled()) {
            logger.debug("Loading XSLT stylesheet from '" + url + "'");
        }
        try {
			/* Для того чтобы не переопределять процедуру необходимо воспользоваться ResourceLoader для динамической обработки ресурсов,
			 * а уже в контроллере определить конкретный ресурс, который сделать доступным getResource контекста приложения.
			 * За обработку ресурсов отвечает отвечает собственный ResourceHandler, который добавляется при помощи addResourceHandlers в WebMvcConfigurerAdapter
			 * Посмотреть комбинацию ResourceHandler + ResourceLoader + getResource */

			/* Если не переопределять эту процедуру, то необходимо реализовать собственный контекст приложения (переопределить ApplicationContext)
			 * и далее с помощью @Bean(name="conversionService") public ConversionService getConversionService(){...} определить конвертор Converter<String, Resource> */

            XSLTResourceInjection loaderXSLTDynRes = getApplicationContext().getBean( "XSLTRes", XSLTResourceInjection.class );     // ПРАВИЛЬНОЕ ИСПОЛЬЗОВАНИЕ Bean-а
            Resource resource = loaderXSLTDynRes.getResource( url );

            //Resource resource = (Resource) getApplicationContext().getBean( url );
            //Resource resource = getApplicationContext().getResource( url );

            System.out.println( "Получен ресурс представления:   [ " + resource + " ]" );
            return new StreamSource(resource.getInputStream(), resource.getURI().toASCIIString());
        } catch (IOException ex) {
            throw new ApplicationContextException("Can't load XSLT stylesheet from '" + url + "'", ex);
        }

    }
}
