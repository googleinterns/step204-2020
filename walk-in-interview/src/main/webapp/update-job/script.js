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