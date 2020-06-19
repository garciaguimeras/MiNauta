function(username, password, captcha) {
    usernameElem = document.getElementById('username');
    passwordElem = document.getElementById('password');
    captchaElem = document.getElementById('captcha');
    button = document.getElementsByName('btn_submit')[0];

    usernameElem.value = username;
    passwordElem.value = password;
    captchaElem.value = captcha;
    button.click();
}