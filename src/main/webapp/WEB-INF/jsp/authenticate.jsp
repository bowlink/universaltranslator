<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<div class="login-container" style="width:500px; margin-left: -250px;">
    <div class="login clearfix">
        <header class="login-header" role="banner">
            <div class="login-header-content">
                <span class="white h1">Two-Factor Authentication</span>
            </div>
        </header>
        <div class="alert alert-danger center-text newCodeMsg" role="alert" style="display:none;"></div>
        <fieldset name="login-fields" form="form-admin-login" class="basic-clearfix">
            <p>We sent a verification code to the email address attached to your account.</p>
            <div class="form-group ${not empty error ? 'has-error' : '' }">
                <label class="control-label" for="username">Verification Code</label>
                <input type="text" class="form-control verificationCode" placeholder="Verification Code" name="verificationCode" maxlength="20" value="" autofocus="true" autocomplete="off" />
            </div>
            <input type="button" value="Verify and sign in" class="btn btn-lg btn-primary btn-block verifyCode" role="button"/>
        </fieldset>
    </div>
    <p class="login-note"><a href="javascript:void(0);" class="resendCode" title="Send New Authentication Code">Send New Authentication Code</a></p>
</div>
