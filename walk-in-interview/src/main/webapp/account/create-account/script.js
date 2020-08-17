/**
 * This file is specific to create-account/index.html.
 * It renders the fields on the page dynamically
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../strings.en.js';

const STRINGS = AppStrings['create-account'];
const CREATE_APPLICANT_ACCOUNT_PAGE = './applicant/index.html';
const CREATE_BUSINESS_ACCOUNT_PAGE = './business/index.html';
const HOMEPAGE_PATH = '../../index.html';

window.onload = () => {
  renderPageElements();
};

/** Adds all the button text to the fields on this page. */
function renderPageElements() {
  const applicantButton = document.getElementById('create-applicant-account');
  applicantButton.innerText = STRINGS['create-applicant-account'];

  const businessButton = document.getElementById('create-business-account');
  businessButton.innerText = STRINGS['create-business-account'];

  const backButton = document.getElementById('back');
  backButton.innerText = STRINGS['back'];
}

const applicantButton = document.getElementById('create-applicant-account');
applicantButton.addEventListener('click', (_) => {
  window.location.href = CREATE_APPLICANT_ACCOUNT_PAGE;
});

const businessButton = document.getElementById('create-business-account');
businessButton.addEventListener('click', (_) => {
  window.location.href = CREATE_BUSINESS_ACCOUNT_PAGE;
});

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = HOMEPAGE_PATH;
});
