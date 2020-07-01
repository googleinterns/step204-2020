
/**
 * Adds a new job listing by making a POST request to the /jobs servlet.
 * @return {boolean} If the request given the parameters was valid or not.
 */
function addJob() {
  event.preventDefault();

  const formElements = document.forms['new-job-form'];
  console.log('forrm', formElements.elements);

  const name = formElements['new-job-title'].value;
  const address = formElements['new-job-address'].value;
  const description = formElements['new-job-description'].value;
  const payFrequency = formElements['new-job-pay-frequency'].value;

  const payMin = formElements['new-job-pay-min'].valueAsNumber;
  const payMax = formElements['new-job-pay-max'].valueAsNumber;
  if (payMin > payMax) {
    return false;
  }

  const requirementsCheckboxes = formElements['requirements-list'];
  const requirementsList = [];
  requirementsCheckboxes.forEach(({checked, id}) => {
      checked? requirementsList.push(id) : '';
  });

  const expiry = formElements['new-job-expiry'].value;
  const duration = formElements['new-job-duration'].value;

  const jobDetails = {
    jobName: name,
    jobLocation: {
      address: address,
      lat: 1.3039, // TODO(issue/13)
      lon: 103.8358,
    },
    jobDescription: description,
    jobPay: {
      frequency: payFrequency,
      min: payMin,
      max: payMax,
    },
    requirements: requirementsList,
    postExpiry: expiry,
  };

  duration !== '' ? jobDetails.jobDuration = duration : '';
  console.log('requestbody', jobDetails);

  fetch('/jobs', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(jobDetails),
  }).then((response) => response.text()).then((data) => {
    return data.status === 200;
  });
}
