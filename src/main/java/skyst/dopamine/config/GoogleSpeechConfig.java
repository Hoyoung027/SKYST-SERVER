package skyst.dopamine.config;


import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;

@Configuration
public class GoogleSpeechConfig {

    @Value("${google.speech.credentials-location}")
    private Resource credentialsResource;

    @Bean
    public SpeechClient speechClient() throws IOException {
        GoogleCredentials creds = GoogleCredentials
                .fromStream(credentialsResource.getInputStream()) // 여기서 API KEY를 불러옴
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        SpeechSettings settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(creds))
                .build();

        return SpeechClient.create(settings);
    }
}