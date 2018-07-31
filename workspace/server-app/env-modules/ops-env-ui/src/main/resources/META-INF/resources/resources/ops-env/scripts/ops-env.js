Argus.System.AddressInput = {
    onItemSelect: function (clientId) {
        if (typeof clientId !== 'undefined' && clientId != null) {
            var input = $('#' + clientId);
            input.val(input.val() + ", ");
        }
    },
    validate: function (clientId, isValid) {
        if (typeof clientId !== 'undefined' && clientId != null && typeof isValid !== 'undefined' && isValid != null) {
            var input = $('#' + clientId);
            if (isValid) {
                input.removeClass('ui-state-error');
            } else {
                input.addClass('ui-state-error');
            }
        }
    }
};