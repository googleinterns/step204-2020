/**
 * This file is specific to update-job.html. It renders the fields on the
 * page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

const CurrentLocale = 'en';

window.onload = () => {
    addPageElements(); // how to deal with job id?????
};

/**
 * Adds all the titles to the fields on this page.
 * 
 * @param {String} jobId 
 */
function addPageElements(jobId) {
    const cancelButton = document.getElementById('job-cancel');
    cancelButton.setAttribute("value", "Cancel");
    cancelButton.setAttribute("type", "reset");

    const submitButton = document.getElementById("job-submit");
    submitButton.setAttribute("value","Create");
    submitButton.setAttribute("type", "submit");

    const job = getJobFromId(jobId);

    const jobTitle = document.getElementById("job-title");
    const jobTitleContent = job.jobTitle;
    jobTitle.setAttribute("value", jobTitleContent);

    const jobDescription = document.getElementById("job-description");
    const jobDescriptionContent = job.jobDescription;
    jobDescription.setAttribute("value", jobDescriptionContent);

    const jobAddress = document.getElementById("job-address");
    const jobAddressContent = job.jobLocation.address;
    jobAddress.setAttribute("value", jobAddressContent);
    
    // TODO: requirement list

    const jobPayFrequency = document.getElementById("job-pay-frequency");
    // TODO
    
    const jobPayMin = document.getElementById("job-pay-min");
    const jobPayMinContent = job.jobPay.min;
    jobPayMin.setAttribute("value", jobPayMinContent);

    const jobPayMax = document.getElementById("job-pay-max");
    const jobPayMaxContent = job.jobPay.max;
    jobPayMax.setAttribute("value", jobPayMaxContent);

    const jobDuration = document.getElementById("job-duration");
    // TODO

    const jobExpiry = document.getElementById("job-expiry");
    const jobExpiryTimestamp = job.postExpiryTimestamp;
    // Multiplied by 1000 so that the argument is in milliseconds, not seconds.
    var expiryDate = new Date(jobExpiryTimestamp * 1000);
    jobExpiry.setAttribute("value", expiryDate);
}

/**
 * Gets the Job post given its id.
 * 
 * @param {String} jobId Cloud Firestore Job Id.
 * @returns Job json.
 */
function getJobFromId(jobId) {
    const url = "/jobs?jobId=" + jobId;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    })
    .then(response => response.json())
    .catch(error => {

    })
}