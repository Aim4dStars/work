/**
 * @namespace
 * @desc root namespace for the application
 */
this.org = this.org || {};
/**
 * @namespace
 * @desc bt specific implementations
 */
org.bt = {
    /**
     * @namespace
     * @desc provides support to localization
     */
    i18n:{},
    /**
     * @namespace
     * @desc all the modules should be under this namespace
     */
    modules:{},
    /**
     * @namespace
     * @desc all the utils should be under this namespace
     */
    utils:{},
    /**
     * @namespace
     * @desc all the global scope data structures should be under this namespace
     */
    collections:{},
    /**
     * @namespace
     * @desc holds the token to send for each req.
     */
    token:null
};