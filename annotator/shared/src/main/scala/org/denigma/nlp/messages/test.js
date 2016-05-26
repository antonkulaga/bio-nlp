
var bratLocation = 'http://weaver.nlplab.org/~brat/demo/v1.3';
head.js(
    // External libraries
    bratLocation + '/client/lib/jquery.min.js',
    bratLocation + '/client/lib/jquery.svg.min.js',
    bratLocation + '/client/lib/jquery.svgdom.min.js',


    // brat helper modules
    bratLocation + '/client/src/configuration.js',
    bratLocation + '/client/src/util.js',
    bratLocation + '/client/src/annotation_log.js',
    bratLocation + '/client/lib/webfont.js',
    // brat modules
    bratLocation + '/client/src/dispatcher.js',
    bratLocation + '/client/src/url_monitor.js',
    bratLocation + '/client/src/visualizer.js'
);

var webFontURLs = [
    bratLocation + '/static/fonts/Astloch-Bold.ttf',
    bratLocation + '/static/fonts/PT_Sans-Caption-Web-Regular.ttf',
    bratLocation + '/static/fonts/Liberation_Sans-Regular.ttf'
];

head.ready(function() {
    // Evaluate the code from the examples and show it to the user
    eval($('#embedding-entity-coll').text());
    eval($('#embedding-entity-doc').text());
    /* Make damn sure to copy the objects before handing them to brat
     since we will modify them later on */
    Util.embed('embedding-entity-example', $.extend({}, collData),
        $.extend({}, docData), webFontURLs);

    eval($('#embedding-attribute-coll').text());
    eval($('#embedding-attribute-doc').text());
    Util.embed('embedding-attribute-example', $.extend({}, collData),
        $.extend({}, docData), webFontURLs);

    eval($('#embedding-relation-coll').text());
    eval($('#embedding-relation-doc').text());
    Util.embed('embedding-relation-example', $.extend({}, collData),
        $.extend({}, docData), webFontURLs);

    eval($('#embedding-event-coll').text());
    eval($('#embedding-event-doc').text());
    Util.embed('embedding-event-example', $.extend({}, collData),
        $.extend({}, docData), webFontURLs);

    // Fuck it! We'll do it live!
    var collInput = $('#coll-input');
    var docInput = $('#doc-input');
    var liveDiv = $('#embedding-live-example');

    // Time for some "real" brat coding, let's hook into the dispatcher
    var liveDispatcher = Util.embed('embedding-live-example',
        $.extend({'collection': null}, collData),
        $.extend({}, docData), webFontURLs);

    var renderError = function() {
        // liveDiv.css({'border': '2px solid red'}); // setting this blows the layout
        collInput.css({'border': '2px solid red'});
        docInput.css({'border': '2px solid red'});
    };

    liveDispatcher.on('renderError: Fatal', renderError);

    var collInputHandler = function() {
        var collJSON;
        try {
            collJSON = JSON.parse(collInput.val());
            collInput.css({'border': '2px inset'});
        } catch (e) {
            // Not properly formatted JSON...
            collInput.css({'border': '2px solid red'});
            return;
        }

        try {
            liveDispatcher.post('collectionLoaded',
                [$.extend({'collection': null}, collJSON)]);
            //liveDiv.css({'border': '2px inset'});  // setting this blows the layout
            docInput.css({'border': '2px inset'});
        } catch(e) {
            console.error('collectionLoaded went down with:', e);
            //liveDiv.css({'border': '2px solid red'});
            collInput.css({'border': '2px solid red'});
        }
    };

    var docInputHandler = function() {
        var docJSON;
        try {
            docJSON = JSON.parse(docInput.val());
            docInput.css({'border': '2px inset'});
        } catch (e) {
            docInput.css({'border': '2px solid red'});
            return;
        }

        try {
            liveDispatcher.post('requestRenderData', [$.extend({}, docJSON)]);
            // liveDiv.css({'border': '2px inset'});  // setting this blows the layout
            collInput.css({'border': '2px inset'});
        } catch(e) {
            console.error('requestRenderData went down with:', e);
            // liveDiv.css({'border': '2px solid red'});
            collInput.css({'border': '2px solid red'});
        }
    };

    // Inject our current example as a start
    var collJSON = JSON.stringify(collData, undefined, '    ');
    docJSON = JSON.stringify(docData, undefined, '    ')
    // pack those just a bit
    var packJSON = function(s) {
        // replace any space with ' ' in non-nested curly brackets
        s = s.replace(/(\{[^\{\}\[\]]*\})/g,
            function(a, b) { return b.replace(/\s+/g, ' '); });
        // replace any space with ' ' in [] up to nesting depth 1
        s = s.replace(/(\[(?:[^\[\]\{\}]|\[[^\[\]\{\}]*\])*\])/g,
            function(a, b) { return b.replace(/\s+/g, ' '); });
        return s
    }
    collInput.text(packJSON(collJSON));
    docInput.text(packJSON(docJSON));

    var listenTo = 'propertychange keyup input paste';
    collInput.bind(listenTo, collInputHandler);
    docInput.bind(listenTo, docInputHandler);
});
