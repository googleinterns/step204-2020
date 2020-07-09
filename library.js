const webdriver = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const path = require('chromedriver').path;

const service = new chrome.ServiceBuilder(path).build();
chrome.setDefaultService(service);

const By = webdriver.By;
const unti = webdriver.until;

const driver = new webdriver.Builder()
    .withCapabilities(webdriver.Capabilities.chrome())
    .build();

driver.get('https://library-app.firebaseapp.com/');

driver.findElements(By.css('input')).then((el) => {
    console.log('found it', el);
});
driver.findElement(By.css('.btn-primary')).getText().then((text) => {
    console.log('text:', text);
})

// driver.sleep(10000);

/** once it finds the elements above then it will close the browser */
// driver.quit();

