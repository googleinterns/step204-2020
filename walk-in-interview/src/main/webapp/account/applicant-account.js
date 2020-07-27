/**
 * This file is specific to account/applicant-account.html. 
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../strings.en.js';

const HOMEPAGE_PATH = '../index.html';
const STRINGS = AppStrings['applicant'];

window.onload = () => {
  let accountId = getId();
  renderPageElements(accountId);
};

/** Gets the ID of this account. */
function getId() {
  // TODO
  return 'xxxxx';
}

/**
 * Adds all the text element in the page.
 * 
 * @param {String} accountId Id of this account.
 */
function renderPageElements(accountId) {
  const backButton = document.getElementById('back');
  backButton.innerText = STRINGS['back'];

  const editButton = document.getElementById('edit');
  editButton.innerText = STRINGS['edit'];

  var accountDetails = getAccountDetails(accountId);

  const name = document.getElementById('name');
  name.innerText = accountDetails.name;

  const requirementsTitle = document.getElementById('requirements-title');
  requirementsTitle.innerText = STRINGS['requirements-title'];

  renderRequirements(accountDetails.requirements);
}

/**
 * Gets the account detail from the database
 * 
 * @param {String} accountId Id of this account.
 * @returns Detail json.
 */
function getAccountDetails(accountId) {
  let accountDetails = {
    name: 'test',
    requirements: [
      'test1',
      'test2',
    ],
  };

  // if there is no details related to such id, return empty details
  // (TODO)
  return accountDetails;
}

function renderRequirements(requirements) {
  const requirementsListElement =
    document.getElementById('requirements');

  // resets the list so we don't render the same requirements twice
  requirementsListElement.innerHTML = '';
  const requirementElementTemplate =
    document.getElementById('requirement-element-template');
  
  for (var i = 0; i < requirements.length; i++) {
    const requirement = requirements[i];
    const requirementElement = requirementElementTemplate
      .cloneNode( /* includes child elements */ true);

    const div = requirementElement.children[0];
    div.setAttribute('id', requirement);
    div.innerText = requirement;

    requirementsListElement.appendChild(requirementElement);
  }
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = HOMEPAGE_PATH;
});

const editButton = document.getElementById('edit');
editButton.addEventListener('click', (_) => {

});
