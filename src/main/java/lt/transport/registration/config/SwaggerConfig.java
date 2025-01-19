package lt.transport.registration.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transporto priemonių valdymo API")
                        .version("1.0")
                        .description("Transporto priemonių registravimo, savininko pakeitimo, informacijos apie vieną transporto priemonę gavimo pagal ID, informacijos apie visas transporto priemones gavimo ir įrašų pašalinimo, pažymint juos kaip negaliojančiais, sistemos API dokumentacija"));
    }
}