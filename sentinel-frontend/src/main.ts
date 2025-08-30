import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { AuthService } from './app/services/auth.service';

bootstrapApplication(AppComponent, appConfig)
  .then(ref => {
    const injector = (ref as any).injector ?? (ref as any)._injector;
    injector.get(AuthService).initFromStorage();
  })
  .catch((err) => console.error(err));
