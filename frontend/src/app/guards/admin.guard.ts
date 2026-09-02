import { CanActivateFn, Router } from '@angular/router';
import Keycloak from 'keycloak-js';
import { inject } from '@angular/core';
import { hasAdminRole } from '../util/auth';


export const adminGuard: CanActivateFn = () => {
  const keycloak = inject(Keycloak);
  const router = inject(Router);

  if (hasAdminRole(keycloak)) {
    return true;
  }

  return router.createUrlTree(["/"]);
}
