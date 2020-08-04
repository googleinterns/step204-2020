/**
 * This file is specific to account/business/index.html. 
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Account id is not involved in the client side
 * 
 * stretch: 
 * 1) show account detail
 * GET '/applicant'
 * 
 * 2) edit account info
 * use cookie to pass the account info to edit page
 */

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../strings.en.js';
import {API} from '../../apis.js';
import {JOB_ID_PARAM, BUSINESS_ID_PARAM, BUSINESS_DETAILS_PARAM, 
  getRequirementsList, setErrorMessage} from '../../common-functions.js';

const HOMEPAGE_PATH = '../../index.html';
const EDIT_ACCOUNT_PATH = './edit/index.html';
const JOB_DETAILS_PATH = '../job-details/index.html';
const STRINGS = AppStrings['business'];
const ACCOUNT_STRINGS = AppStrings['account'];

// TODO(issue/34): implement pagination for job listings
const DEFAULT_PAGE_SIZE = 20;
const DEFAULT_PAGE_INDEX = 0;


window.onload = () => {
  renderPageElements();
};

/**
 * Adds text into the page.
 */
function renderPageElements() {
  var accountDetails = getAccountDetails();

  const backButton = document.getElementById('back');
  backButton.innerText = ACCOUNT_STRINGS['back'];

  const editForm = document.getElementById('edit-form');
  editForm.method = 'GET';
  editForm.action = EDIT_ACCOUNT_PATH;

  const applicantIdElement = document.getElementById('business-id');
  applicantIdElement.setAttribute('type', 'hidden');
  applicantIdElement.setAttribute('name', BUSINESS_ID_PARAM);
  applicantIdElement.setAttribute('value', accountId);

  const applicantDetailElement = document.getElementById('business-details');
  applicantDetailElement.setAttribute('type', 'hidden');
  applicantDetailElement.setAttribute('name', BUSINESS_DETAILS_PARAM);
  applicantDetailElement.setAttribute('value', JSON.stringify(accountDetails));

  const editButton = document.getElementById('edit');
  editButton.setAttribute('value', ACCOUNT_STRINGS['edit']);
  editButton.setAttribute('type', 'submit');
  // in case it was disabled earlier
  editButton.disabled = false;

  const nameLabel = document.getElementById('name-label');
  nameLabel.innerText = STRINGS['name'];
  const name = document.getElementById('name');
  name.innerText = accountDetails.name;

  const jobsTitle = document.getElementById('job-listings-title');
  jobsTitle.innerText = STRINGS['active-job-posts-title'];

  renderActiveJobPosts(DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX);
}

/**
 * Gets the account detail from the database
 * 
 * @returns Detail json.
 */
function getAccountDetails() {
  const businessId = getId();
  let accountDetails = {
    accountId: businessId,
    name: 'test',
    jobs: [
      'jobId1',
      'jobId2',
    ],
  };

  // if there is no details related to such id, return empty details
  // (TODO)
  return accountDetails;
}

/**
 * Renders the active job posts listing.
 * @param {int} pageSize The number of jobs for one page.
 * @param {*} pageIndex The page index (starting from 0).
 */
function renderActiveJobPosts(pageSize, pageIndex) {
  const url = `${API['business-jobs-list']}?pageSize=${pageSize}&pageIndex=${pageIndex}`;
  fetch(url, {
    method: 'GET',
    headers: {'Content-Type': 'application/json'},
  })
    .then((response) => response.json())
    .then((jobPageData) => {
      displayJobListings(jobPageData);
    })
    .catch((error) => {
      console.error('error fetching job listings', error);
      setErrorMessage(/* errorMessageElementId= */ 'error-message',
          /* msg= */ STRINGS['get-jobs-error-message'],
          /* include default msg= */ false);
    });
}
/**
 * This will add all the job listings onto the homepage.
 * @param {Object} jobPageData The details to be shown on the homepage.
 */
function displayJobListings(jobPageData) {
  const jobListingsElement = document.getElementById('job-listings');
  const jobShowing = document.getElementById('job-listings-showing');

  /* reset the list so we don't render the same jobs twice */
  jobListingsElement.innerHTML = '';
  jobShowing.innerHTML = '';

  if (jobPageData === undefined ||
    !jobPageData.hasOwnProperty('jobList') ||
    jobPageData.jobList.length === 0) {
    setErrorMessage(/* errorMessageElementId= */ 'error-message',
        /* msg= */ STRINGS['no-jobs-error-message'],
        /* includesDefaultMsg= */ false);
    return;
  }

  const jobListings = jobPageData['jobList'];

  jobListings.forEach((job) => {
    jobListingsElement.appendChild(buildJobElement(job));
  });

  jobShowing.innerText = `${jobPageData.range.minimum} -` +
    ` ${jobPageData.range.maximum} ${STRINGS['job-listings-showing']} ` +
    `${jobPageData.totalCount}`;
}


/**
 * Builds the job element given the job details from the servlet response.
 * @param {Object} job The job to be displayed.
 * @return {Element} The job listing element.
 */
function buildJobElement(job) {
  const jobPostPreviewTemplate =
    document.getElementById('job-listing-template');

  const jobPost =
    jobPostPreviewTemplate.cloneNode( /* and child elements */ true);
  jobPost.setAttribute('id', job.jobId);

  const jobTitle = jobPost.children[0];
  jobTitle.innerText = job.jobTitle;

  const jobAddress = jobPost.children[1];
  const location = job.jobLocation;
  jobAddress.innerText = `${location.address}, ${location.postalCode}`;

  const jobPay = jobPost.children[2];
  const pay = job.jobPay;
  jobPay.innerText = `${pay.min} - ${pay.max} SGD ` +
  `(${pay.paymentFrequency.toLowerCase()})`;

  const requirementsList = jobPost.children[3];
  const fullRequirementsList = getRequirementsList();
  const requirementsArr = [];

  const jobRequirements = job.requirements;
  for (const key in jobRequirements) {
    if (jobRequirements.hasOwnProperty(key)) {
      if (jobRequirements[key] /* is true */) {
        requirementsArr.push(fullRequirementsList[key]);
      }
    }
  }

  requirementsList.innerText =
  `Requirements List: ${requirementsArr.join(', ')}`;

  const detailsForm = jobPost.children[4];
  detailsForm.method = 'GET';
  detailsForm.action = JOB_DETAILS_PATH;

  const jobIdElement = jobPost.children[4].children[0];
  jobIdElement.setAttribute('type', 'hidden');
  jobIdElement.setAttribute('name', JOB_ID_PARAM);
  const jobId = job[JOB_ID_PARAM];
  jobIdElement.setAttribute('value', jobId);

  jobPost.addEventListener('click', (_) => {
    if (jobId === '') {
      throw new Error('jobId should not be empty');
    }
    detailsForm.submit();
  });

  return jobPost;
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = HOMEPAGE_PATH;
});
