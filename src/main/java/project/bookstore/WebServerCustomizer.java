package project.bookstore;

import org.apache.catalina.User;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import project.bookstore.domain.exception.UserException;


@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        ErrorPage UserError = new ErrorPage(UserException.class, "/error-page/404");

        factory.addErrorPages(UserError);
    }
}
