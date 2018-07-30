//
// Primefaces: AutoComplete suggestions panel position fix by Hatem Alimam 
//

$(function () {
    PrimeFaces.widget.AutoComplete.prototype.alignPanel = function () {
        var fixedPosition = this.panel.css('position') == 'fixed',
            win = $(window),
            positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null,
            panelWidth = null;

        if (this.cfg.multiple) {
            panelWidth = this.multiItemContainer.innerWidth() - (this.input.position().left - this.multiItemContainer.position().left);
        }
        else {
            panelWidth = this.input.innerWidth();
        }

        if (this.items.length > 6) {
            panelHeight = 250;
        } else {
            panelHeight = "auto";
        }

        this.panel.css({
            left: '',
            top: '',
            width: panelWidth,
            height: panelHeight,
            zIndex: PrimeFaces.zindex
        })
            .position({
                my: 'left top'
                , at: 'left bottom'
                , of: this.input
                , collision: 'none',
                offset: positionOffset
            });
    };

    PrimeFaces.widget.SelectOneMenu.prototype.alignPanel = function () {
        this.alignPanelWidth();
        if (this.panel.parent().is(this.jq)) {
            this.panel.css({left: 0, top: this.jq.innerHeight()})
        } else {
            if (typeof this.filterInput != 'undefined' &&  this.filterInput != null) {
                // В этой ветке положение панели выпадающего меню selectOne c атрибутом filter=true. Положение панели верхний левый угол поля
                this.panel.css({left: "", top: ""}).position({
                    my: "left top",
                    at: "left top",
                    of: this.jq,
                    collision: "flipfit"
                })
            } else {
                this.panel.css({left: "", top: ""}).position({
                    my: "left top",
                    at: "left bottom",
                    of: this.jq,
                    collision: "flipfit"
                })
            }
        }
        var $input = this.jq;
        if ($input.closest('.ui-dialog').length) {
            this.panel.css({position: 'fixed'})
        } else {
            this.panel.css({position: 'absolute'})
        }
    },

    PrimeFaces.widget.SelectCheckboxMenu.prototype.alignPanel = function () {
        var fixedPosition = this.panel.css('position') == 'fixed',
            win = $(window),
            positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null,
            panelStyle = this.panel.attr('style');

        this.panel.css({
            'left': '',
            'top': '',
            'z-index': ++PrimeFaces.zindex
        });

        if (this.panel.parent().attr('id') === this.id) {
            this.panel.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            if (typeof this.filterInput != 'undefined' &&  this.filterInput != null) {
                this.panel.position({
                    my: 'left top'
                    , at: 'left top'
                    , of: this.jq
                    , offset: positionOffset
                });
            } else {
                this.panel.position({
                    my: 'left top'
                    , at: 'left bottom'
                    , of: this.jq
                    , offset: positionOffset
                });
            }
        }

        if (!this.widthAligned && (this.panel.width() < this.jq.width()) && (!panelStyle || panelStyle.toLowerCase().indexOf('width') === -1)) {
            this.panel.width(this.jq.width());
            this.widthAligned = true;
        }
        var $input = this.jq;
        if ($input.closest('.ui-dialog').length) {
            this.panel.css({position: 'fixed'})
        } else {
            this.panel.css({position: 'absolute'})
        }
    }
});

// Этот фикс для того, чтобы в компоненте fieldset поверх кликабельного legend появились кнопки toolbar и синхронно с панелью появлялись и прятались при сворачивании/разворачивании.
/**
 * PrimeFaces Fieldset Widget
 */
PrimeFaces.widget.Fieldset = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.legend = this.jq.children('.ui-fieldset-legend');

        var $this = this;

        if(this.cfg.toggleable) {
            this.content = this.jq.children('.ui-fieldset-content');
            this.toolbar = this.content.find('.type-content-outer');
            this.toggler = this.legend.children('.ui-fieldset-toggler');
            this.stateHolder = $(this.jqId + '_collapsed');

            //Add clickable legend state behavior
            this.legend.on('click', function(e) {
                $this.hideToolbar();
                $this.toggle(e);
            })
                .on('mouseover', function() {
                    $this.legend.toggleClass('ui-state-hover');
                })
                .on('mouseout', function() {
                    $this.legend.toggleClass('ui-state-hover');
                })
                .on('mousedown', function() {
                    $this.legend.toggleClass('ui-state-active');
                })
                .on('mouseup', function() {
                    $this.legend.toggleClass('ui-state-active');
                })
                .on('focus', function() {
                    $this.legend.toggleClass('ui-state-focus');
                })
                .on('blur', function() {
                    $this.legend.toggleClass('ui-state-focus');
                })
                .on('keydown', function(e) {
                    var key = e.which,
                        keyCode = $.ui.keyCode;

                    if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                        $this.toggle(e);
                        e.preventDefault();
                    }
                });
        }
    },

    /**
     * Toggles the content
     */
    toggle: function(e) {
        this.updateToggleState(this.cfg.collapsed);

        var $this = this;
        this.content.slideToggle(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            $this.showToolbar();
            if($this.cfg.behaviors) {
                var toggleBehavior = $this.cfg.behaviors['toggle'];

                if(toggleBehavior) {
                    toggleBehavior.call($this);
                }
            }
        });

        PrimeFaces.invokeDeferredRenders(this.id);
    },

    hideToolbar: function() {
        if(!this.toolbar.hasClass('hide-toolbar')){
            this.toolbar.addClass('hide-toolbar');
        }
    },

    showToolbar: function() {
        if(this.toolbar.hasClass('hide-toolbar')){
            this.toolbar.removeClass('hide-toolbar');
        }
    },

    /**
     * Updates the visual toggler state and saves state
     */
    updateToggleState: function(collapsed) {
        if(collapsed){
            this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
        }
        else {
            this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');
        }

        this.cfg.collapsed = !collapsed;

        this.stateHolder.val(!collapsed);
    }

});
