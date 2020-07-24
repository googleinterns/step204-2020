/**
 * This file is specific to log-in/log-in.html. 
 * It renders the fields on the page dynamically
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../strings.en.js';

const STRINGS = AppStrings['applicant-log-in'];

window.onload = () => {
  renderPageElements();
}

/** Adds all the text to the fields on this page. */
function renderPageElements() {
  const accountElement = document.getElementById('account');

  const accountLabelElement = accountElement.children[0];
  accountLabelElement.innerText = STRINGS['account'];

  const accountInputElement = accountElement.children[1];
  accountInputElement.setAttribute('type', 'text');
  // TODO: send the input to the log-in related stuff

  const passwordElement = document.getElementById('password');

  const passwordLabelElement = passwordElement.children[0];
  passwordLabelElement.innerText = STRINGS['password'];

  const passwordInputElement = passwordElement.children[1];
  passwordInputElement.setAttribute('type', 'text');
  // TODO: send the input to the log-in related stuff

  const otpButton = document.getElementById('otp');
  otpButton.innerText = STRINGS['otp'];
}

const otpButton = document.getElementById('otp');
otpButton.addEventListener('click', (_) => {
  // TODO: send a OTP sms
});
