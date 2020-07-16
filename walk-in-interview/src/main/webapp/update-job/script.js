/**
 * This file is specific to update-job.html. It renders the fields on the
 * page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

 // TODO(issue/21): get the language from the browser
const CurrentLocale = "en";

import {getRequirementsList, getJobDetailsFromUserInput, 
    validateRequiredUserInput, setErrorMessage} from "../new-job/script.js";

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from "/strings.en.js";

const STRINGS = AppStrings["update-job"];
const HOMEPAGE_PATH = "../job-details/index.html";
const RESPONSE_ERROR = "There was an error while loading the job post. Please try again";

window.onload = () => {
    var jobId = getJobId();
    addPageElements(jobId);
};

/**
 * Gets the jobId from job-details/index.html page.
 */
function getJobId() {
    var idx = document.URL.indexOf("?");
    var jobId = "";
    if (idx != -1) {
        setErrorMessage(/* msg */ "No Job Id found. " + RESPONSE_ERROR,
            /** include default msg */ false);
    }
    
    var jobIdPair = document.URL.substring(idx + 1, document.URL.length).split("&");
    nameVal = jobIdPair[0].split("=");
    jobId = nameVal[1];

    return jobId;
}

/**
 * Adds all the titles to the fields on this page.
 * 
 * @param {String} jobId 
 */
function addPageElements(jobId) {
    if (jobId === "") {
        setErrorMessage(/* msg */ "Empty Job Id found. " + RESPONSE_ERROR,
            /** include default msg */ false);
        return;
    }

    const cancelButton = document.getElementById("cancel");
    cancelButton.setAttribute("value", STRINGS["cancel"]);
    cancelButton.setAttribute("type", "reset");

    const jobPageTitle = document.getElementById("page-title");
    jobPageTitle.innerText = STRINGS["page-title"];

    const submitButton = document.getElementById("update");
    submitButton.setAttribute("value", STRINGS["update"]);
    submitButton.setAttribute("type", "submit");

    const job = getJobFromId(jobId);

    const jobTitle = document.getElementById("title");
    const jobTitleContent = job.jobTitle;
    jobTitle.setAttribute("type", "text");
    jobTitle.setAttribute("value", jobTitleContent);

    const jobDescription = document.getElementById("description");
    const jobDescriptionContent = job.jobDescription;
    jobDescription.setAttribute("type", "text");
    jobDescription.setAttribute("value", jobDescriptionContent);

    const jobAddress = document.getElementById("address");
    const jobAddressContent = job.jobLocation.address;
    jobAddress.setAttribute("type", "text");
    jobAddress.setAttribute("value", jobAddressContent);

    const postalCode = document.getElementById('postal-code');
    const postalCodeContent = job.jobLocation.postalCode;
    postalCode.setAttribute("type", "text");
    postalCode.setAttribute("value", postalCodeContent);
    
    const requirementsTitle = document.getElementById("requirements-title");
    requirementsTitle.innerText = STRINGS["requirements-title"];
    const requirements = job.requirements;
    renderRequirementsList(requirements);

    const payTitle = document.getElementById("pay-title");
    payTitle.innerText = STRINGS["pay-title"];
    const jobPayFrequency = job.jobPay.paymentFrequency;
    renderJobPayFrequencyOptions(jobPayFrequency);
    
    const jobPayMin = document.getElementById("pay-min");
    const jobPayMinContent = job.jobPay.min;
    jobPayMin.setAttribute("type", "number");
    jobPayMin.setAttribute("value", jobPayMinContent);

    const jobPayMax = document.getElementById("pay-max");
    const jobPayMaxContent = job.jobPay.max;
    jobPayMax.setAttribute("type", "number");
    jobPayMax.setAttribute("value", jobPayMaxContent);

    const durationTitle = document.getElementById("duration-title");
    durationTitle.innerText = STRINGS["duration-title"];
    const jobDuration = job.jobDuration;
    renderJobDurationOptions(jobDuration);

    const expiryTitle = document.getElementById("expiry-title");
    expiryTitle.innerText = STRINGS["expiry-title"];
    const jobExpiry = document.getElementById("expiry");
    const jobExpiryTimestamp = job.postExpiryTimestamp;
    jobExpiry.setAttribute("type", "date");
    renderJobExpiryLimits(jobExpiryTimestamp);

    // Resets the error to make sure no error msg initially present.
    setErrorMessage(/* msg */ "", /** includes default msg */ false);
}

/**
 * Gets the Job post given its id.
 * 
 * @param {String} jobId Cloud Firestore Job Id.
 * @returns Job json.
 */
function getJobFromId(jobId) {
    // TODO(issue/53): run the webpage to test once doGet finishes in JobServlet
    const url = "/jobs?jobId=" + jobId;
    fetch(url, {
        method: "GET",
        headers: {"Content-Type": "application/json"},
    })
    .then(response => response.json())
    .catch(error => {
        setErrorMessage(/* msg */ RESPONSE_ERROR,
            /** include default msg */ false);
        console.log("error", error);
    })
}

/**
 * Adds the list of requirements that are stored in the database.
 * 
 * @param {Array} requirements An array of requirements(string) of the job post.
 */
function renderRequirementsList(requirements) {
    const requirementsMap = getRequirementsList();
    const requirementsListElement = document.getElementById("requirements-list");

    // Resets the list in case renders the same requirements twice
    requirementsListElement.innerHTML = "";
    const requirementElementTemplate = document.getElementById("requirement-element-template");

    for (const key in requirementsMap) {
        if (!requirementsMap.hasOwnProperty(key)) {
            continue;
        }

        const requirementElement = requirementElementTemplate.cloneNode(/** includes child elements **/ true);
        requirementElement.setAttribute("id", key);

        // tick box
        const checkbox = requirementElement.children[0];
        checkbox.setAttribute("id", key);
        checkbox.setAttribute("value", key);

        // If this requirement is one of the criteria for this job box, tick it
        tickExistingRequirements(requirementsMap[key], requirements, checkbox);

        // text label
        const label = requirementElement.children[1];
        label.setAttribute("for", key);
        label.innerHTML = requirementsMap[key];

        requirementsListElement.appendChild(requirementElement);
    }
}

/**
 * Ticks the existing requirement.
 * 
 * @param {String} requirement Requirement localized name.
 * @param {String} requirements Requirements list of current job post.
 * @param {HTMLElement} requirementElement HTML checkbox for that requirement
 */
function tickExistingRequirements(requirement, requirements, requirementCheckbox) {
    if (requirements.includes(requirement)) {
        requirementCheckbox.setAttribute("checked", true);
    }
}

/**
 * Dynamically adds the options for job pay frequency.
 * 
 * @param {String} jobPayFrequency Original payment frequency of the current job.
 */
function renderJobPayFrequencyOptions(jobPayFrequency) {
    const jobPaySelectElement = document.getElementById("pay-frequency");
    jobPaySelectElement.setAttribute("required", true);
  
    renderSelectOptions(jobPaySelectElement, STRINGS["pay-frequency"], jobPayFrequency);
}

/**
 * Dynamically add the options for job duration. 
 * 
 * @param {String} jobDuration Original duration of the current job.
 */
function renderJobDurationOptions(jobDuration) {
    const jobDurationSelect = document.getElementById("duration");
    jobDurationSelect.setAttribute("required", true);
  
    renderSelectOptions(jobDurationSelect, STRINGS["duration"], jobDuration);
  }

/**
 * Adds the keys and values from the options map to the select element.
 * Chooses the existing options.
 * 
 * @param {Element} selectElement The select element.
 * @param {Map} options The map of options to be added.
 * @param {String} existingOption Existing options.
 */
function renderSelectOptions(selectElement, options, existingOption) {
    selectElement.options.length = 0;
    selectElement.options[0] = new Option("Select", "");
    selectElement.options[0].setAttribute("disabled", true);
  
    for (const key in options) {
        if (!options.hasOwnProperty(key)) {
            continue;
        }

        var defaultSelected = key == existingOption;
        selectElement.options[selectElement.options.length] = new Option(options[key], key, defaultSelected);
    }
}

/**
 * Dynamically adds the limits for choosing the new job post expiry.
 * 
 * @param {long} jobExpiryTimestamp Timestamp of the expiry date for this job post.
 */
function renderJobExpiryLimits(jobExpiryTimestamp) {
    // Multiplied by 1000 so that the argument is in milliseconds, not seconds.
    const expiryDate = new Date(jobExpiryTimestamp * 1000);
    
    const date = new Date();
    const min = date.toISOString().substr(0, 10);
    date.setFullYear(date.getFullYear() + 1);
    const max = date.toISOString().substr(0, 10);
  
    const datePicker = document.getElementById("expiry");
    datePicker.setAttribute("min", min);
    datePicker.setAttribute("max", max);
    datePicker.setAttribute("type", "date");
    datePicker.setAttribute("value", expiryDate);
  }

const submitButton = document.getElementById("update");
submitButton.addEventListener("click", (_) => {
  if (!validateRequiredUserInput()) {
    return;
  }

  const jobDetails = getJobDetailsFromUserInput();

  fetch('/jobs', {
    method: 'PATCH',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(jobDetails),
  })
      .then((response) => response.text())
      .then((data) => {
        console.log("data", data);
        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* msg */ "", /** include default msg */ false);
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        setErrorMessage(/* msg */ RESPONSE_ERROR,
            /** include default msg */ false);
        console.log("error", error);
      });
});

const cancelButton = document.getElementById("cancel");
cancelButton.addEventListener("click", (_) => {
    window.location.href= HOMEPAGE_PATH;
});
