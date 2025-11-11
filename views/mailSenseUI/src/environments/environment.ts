/**
 * Configuration for the development environment.
 */
export const environment = {
  // Flag indicating that this is the production build (always false for dev)
  production: false,
  
  // API URL for local testing (e.g., a local Node/Express server)
  apiUrl: 'http://localhost:8080/api',
  
  // Example feature toggle: enable extra logging or debugging tools
  debugMode: true,
  
  // Application name (can be useful for titles/branding during dev)
  appName: 'MailSenseUI (DEV)'
};