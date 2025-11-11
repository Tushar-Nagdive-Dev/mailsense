/**
 * Configuration for the production environment.
 */
export const environment = {
  // Flag indicating that this is the production build (set to true)
  production: true,
  
  // Secure, final API URL
  apiUrl: 'https://api.mailsense.com/v1',
  
  // Disable debugging in production for security and performance
  debugMode: false,
  
  // Final application name
  appName: 'MailSenseUI'
};