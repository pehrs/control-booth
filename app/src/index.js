import React from 'react';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import ReactDOM from 'react-dom';
import App from './App';
import { CookiesProvider } from 'react-cookie';
import reportWebVitals from './reportWebVitals';
import { AuthProvider } from 'react-oidc-context';
import { QueryClientProvider } from '@tanstack/react-query';

// Importing the Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter } from 'react-router-dom';
import { onSigninCallback, queryClient, userManager } from './config';
import { ProtectedApp } from './ProtectedApp';
import {ErrorBoundary} from 'react-error-boundary';

// ReactDOM.render(<App />, document.getElementById('root'));
const rootElement = document.getElementById('root');
const root = createRoot(rootElement);

const htmlElement = document.querySelector('html');
htmlElement.setAttribute('data-bs-theme', 'dark');

// const { override, addWebpackDevServerConfig } = require('customize-cra');
// module.exports = override(
//   addWebpackDevServerConfig((config) => {
//     config.allowedHosts = 'all';
//     return config;
//   })
// );

const errorHandler = (error, componentStack) => {
  return (
    <pre> {JSON.stringify(error, "", "  ")} </pre>
  );
};

root.render(
  <StrictMode>
    <CookiesProvider>
      <BrowserRouter>
        {/* <AuthProvider userManager={userManager} onSigninCallback={onSigninCallback}>
          <QueryClientProvider client={queryClient}>
            <ProtectedApp> */}
        <ErrorBoundary onError={errorHandler}>
          <App />
        </ErrorBoundary>
        {/* </ProtectedApp>
          </QueryClientProvider>
        </AuthProvider> */}
      </BrowserRouter>
    </CookiesProvider>
  </StrictMode>,
);

reportWebVitals();