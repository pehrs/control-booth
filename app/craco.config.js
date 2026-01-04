// const allowedHosts = process.env.ALLOWED_HOSTS ? process.env.ALLOWED_HOSTS.split(',') : ['gateway.nsa2.com'];
const allowedHosts = process.env.ALLOWED_HOSTS ? process.env.ALLOWED_HOSTS.split(',') : ['control-booth.org'];

module.exports = {
  devServer: (devServerConfig) => {
    // Set allowedHosts so webpack-dev-server accepts requests for your DNS name
    devServerConfig.allowedHosts = allowedHosts;
    return devServerConfig;
  },
};
