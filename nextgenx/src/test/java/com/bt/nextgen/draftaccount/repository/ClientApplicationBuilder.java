package com.bt.nextgen.draftaccount.repository;

import com.bt.nextgen.config.JsonObjectMapper;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.springframework.context.ApplicationContext;

public class ClientApplicationBuilder {


    private Long id = 123L;
    private String productId = "789";

    public static ClientApplicationBuilder aDraftAccount() {
        return new ClientApplicationBuilder();
    }

    public ClientApplication build() {
        ClientApplication clientApplication = new ClientApplication(){
            @Override
            public Long getId() {
                return id;
            }
        };
        clientApplication.setProductId(productId);
        //setup Spring context with 'jsonObjectMapper'
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(new JsonObjectMapper());
        clientApplication.setApplicationContext(applicationContext);
        return clientApplication;
    }

    public ClientApplicationBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ClientApplicationBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }
}
