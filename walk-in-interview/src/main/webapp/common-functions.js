/**
 * This file wraps some commonly used job related function as a reusable module.
 */

/**
 * Gets the requirements list from the servlet
 * (which gets it from the database).
 * @return {Object} the requirements list in a key-value mapping format.
 */
function getRequirementsList() {
    // TODO(issue/17): GET request to servlet to get from database
    // returning some hardcoded values for now
    return {
      'O_LEVEL': 'O Level',
      'LANGUAGE_ENGLISH': 'English',
      'DRIVING_LICENSE_C': 'Category C Driving License',
    };
}

/**
 * Gets job detail from user input.
 * @return {Object} containing the user inputs.
 */
function getJobDetailsFromUserInput() {
    const name = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    const address = document.getElementById('address').value;
    const postalCode = document.getElementById('postal-code').value;
    const payFrequency = document.getElementById('pay-frequency').value;
    const payMin = document.getElementById('pay-min').valueAsNumber;
    const payMax = document.getElementById('pay-max').valueAsNumber;

    const requirementsCheckboxes =
        document.getElementsByName('requirements-list');
    const requirementsList = [];
    requirementsCheckboxes.forEach(({checked, id}) => {
        if (checked) {
        requirementsList.push(id);
        }
    });

    const expiry = document.getElementById('expiry').valueAsNumber;
    const duration = document.getElementById('duration').value;

    const jobDetails = {
        jobTitle: name,
        jobLocation: {
        address: address,
        postalCode: postalCode,
        lat: 1.3039, // TODO(issue/13): get these from places api
        lon: 103.8358,
        },
        jobDescription: description,
        jobPay: {
        paymentFrequency: payFrequency,
        min: payMin,
        max: payMax,
        },
        requirements: requirementsList,
        postExpiryTimestamp: expiry,
        jobDuration: duration,
    };

    return jobDetails;
}

/**
 * Validates the user input
 * @return {boolean} depending on whether the input is valid or not.
 */
function validateRequiredUserInput() {
    // TODO(issue/19): add more validation checks
    const name = document.getElementById('title');
    const description = document.getElementById('description');
    const address = document.getElementById('address');
    const postalCode = document.getElementById('postal-code');
    const payFrequency = document.getElementById('pay-frequency').value;
    const payMin = document.getElementById('pay-min').valueAsNumber;
    const payMax = document.getElementById('pay-max').valueAsNumber;
    const duration = document.getElementById('duration').value;
    const expiry = document.getElementById('expiry').valueAsNumber;

    if (name.value === '') {
        setErrorMessage(/* msg */ name.placeholder,
            /** includes default msg */ true);
        return false;
    }

    if (description.value === '') {
        setErrorMessage(/* msg */ description.placeholder,
            /** includes default msg */ true);
        return false;
    }

    if (address.value === '') {
        setErrorMessage(/* msg */ address.placeholder,
            /** includes default msg */ true);
        return false;
    }

    if (postalCode.value === '') {
        setErrorMessage(/* msg */ postalCode.placeholder,
            /** includes default msg */ true);
        return false;
    }

    if (payFrequency === '' || Number.isNaN(payMin) || Number.isNaN(payMax) ||
        payMin > payMax || payMin < 0 || payMax < 0) {
        setErrorMessage(/* msg */ document.getElementById('pay-title')
            .textContent, /** includes default msg */ true);
        return false;
    }

    if (duration === '') {
        setErrorMessage(/* msg */ document.getElementById('duration-title')
            .textContent, /** includes default msg */ true);
        return false;
    }

    if (Number.isNaN(expiry)) {
        setErrorMessage(/* msg */ document.getElementById('expiry-title')
            .textContent, /** includes default msg */ true);
        return false;
    }

    return true;
}

/**
 * Sets the error message according to the param.
 * @param {String} msg the message that the error div should display.
 * @param {boolean} includesDefault whether the default.
 * @param {String} defaultMessage default messages to be displayed.
 * message should be included.
 */
function setErrorMessage(msg, includesDefault, defaultMessage) {
    document.getElementById('error-message').innerText = (includesDefault ? defaultMessage + msg : msg);
}

export {getRequirementsList, getJobDetailsFromUserInput, validateRequiredUserInput, setErrorMessage};
