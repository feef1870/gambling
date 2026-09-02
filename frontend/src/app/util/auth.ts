import Keycloak from 'keycloak-js';


export function hasAdminRole(keycloak: Keycloak): boolean {
  const roles = keycloak.realmAccess?.roles ?? [];
  return roles.some((role) => role.toLowerCase() === 'admin');
}
