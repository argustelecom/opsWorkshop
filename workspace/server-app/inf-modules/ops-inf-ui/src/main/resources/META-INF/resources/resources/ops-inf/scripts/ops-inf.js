Argus.System.AutoHeight = {
    compute: function (containerId, minusHeight) {

        if (typeof minusHeight == "undefined" || minusHeight == null) {
            minusHeight = 0;
        }

        var container = "#" + containerId;

        var possibleComponentHeight = $(window).height() - $(container).offset().top - minusHeight;
        $(container).height(possibleComponentHeight);
    }
};

Argus.System.BlockExpander = {

    init: function(containerId) {
        this.containerId = $('#' + containerId);
        this.container = this.containerId.find('.contact-person-list');
        this.containerItems = this.container.find('.contact-person-item');
        this.expandIcon = this.containerId.find('i.icon-ellipsis');
        this.expandContainerClass = 'container-open';
        this.collapseContainerClass = 'container-close';
        this.collapsetitle = 'Свернуть';
        this.expandtitle = 'Развернуть';

        this.iconToggler();
    },

    iconToggler: function(){
        if(this.isIconShowHide()) {
            if(this.isContainerClosed()){
                this.addCollapseClass();
            }
        } else {
            this.expandIcon.addClass('visibility-hidden');
        }
    },

    isIconShowHide: function(){
        return this.containerItems.length > 3;
    },

    isContainerClosed: function() {
        return !(this.containerItems.length <= 3);
    },

    addCollapseClass: function(){
        this.container.addClass(this.collapseContainerClass);
    },

    addExpandClass: function(){
        this.container.addClass(this.expandContainerClass);
    },

    removeExpandClass: function(){
        this.container.removeClass(this.expandContainerClass);
    },

    removeCollapseClass : function(){
        this.container.removeClass(this.collapseContainerClass);
    },

    isCollapse: function () {
        return this.container.hasClass(this.collapseContainerClass) ? true : false;
    },

    containerToggler: function() {
        if(this.isCollapse()) {
            this.expandIcon.attr('title', this.collapsetitle);
            this.removeCollapseClass();
            this.addExpandClass();
        } else {
            this.expandIcon.attr('title', this.expandtitle);
            this.removeExpandClass();
            this.addCollapseClass();
        }
    }
};

Argus.System.BlockToggler = {
    toggleComment: function (elem) {
        var $item = $(elem).closest('.contact-list');
        var $commentBlock = $item.find('.contact-comment');

        if($commentBlock.hasClass('closed')){
            $commentBlock.removeClass('closed');
        } else {
            $commentBlock.addClass('closed');
        }
    },

    toggleFilter: function (elem) {
        var $parentContainer = $(elem).closest('.toggle-container');
        var $completeBlock = $parentContainer.find('.complete-block');
        var $shortBlock = $parentContainer.find('.short-block');

        if($completeBlock.hasClass('m-disp-none')){
            $completeBlock.removeClass('m-disp-none');
            $shortBlock.addClass('m-disp-none');
        } else {
            $completeBlock.addClass('m-disp-none');
            $shortBlock.removeClass('m-disp-none');
        }
    }
};

Argus.System.Tooltip = {
    tooltip: function (text, element) {
        $(element).attr('title',text);
    }
};

Argus.System.Scroll = {
    stick: function (stick, scrolled, form) {
        var stickyBlock = document.getElementById(stick);
        var stop = (stickyBlock.offsetTop - 60);


        window.onscroll = function (e) {
            var scrollTop = (window.pageYOffset !== undefined) ? window.pageYOffset : (document.documentElement || document.body.parentNode || document.body).scrollTop;
            // console.log(scrollTop, stickyBlock.offsetTop);
            // stickyBlock.offsetTop;
            var scrolledBlock = document.getElementById(scrolled).offsetHeight;
            var containedForm = document.getElementById(form).offsetHeight;

            if (scrollTop >= stop && scrolledBlock > containedForm) {
                stickyBlock.className = 'stick';
            } else {
                stickyBlock.className = '';
            }

        }
    }
};
