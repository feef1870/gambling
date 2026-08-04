import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {
  createInterceptorCondition,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  IncludeBearerTokenCondition,
  includeBearerTokenInterceptor,
  provideKeycloak,
} from 'keycloak-angular';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

const apiCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
  urlPattern: /^(http:\/\/localhost:8080)(\/.*)?$/i,
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),

    provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
    provideKeycloak({
      config: {
        url: 'http://localhost:8443',
        realm: 'gambling',
        clientId: 'test-client',
      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false,
      },
    }),

    {
      provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
      useValue: [apiCondition],
    },
  ],
};
