import { QueryClient } from '@tanstack/react-query';
import { UserManager, WebStorageStateStore } from 'oidc-client';

export const userManager = new UserManager({
  // authority: "http://auth.nsa2.com:9000/realms/nsa2-realm",
  // client_id: "nsa2-gateway",
  authority: "http://auth.nsa2.com:9000/realms/control-booth",
  client_id: "control-booth",
  redirect_uri: `${window.location.origin}${window.location.pathname}`,
  post_logout_redirect_uri: window.location.origin,
  userStore: new WebStorageStateStore({ store: window.sessionStorage }),
  monitorSession: true, // this allows cross tab login/logout detection
});

export const onSigninCallback = () => {
  window.history.replaceState({}, document.title, window.location.pathname);
};

export const queryClient = new QueryClient();