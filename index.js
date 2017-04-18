var parentURL = 'http://localhost:8080/Shop-war/services/';
document.onload = new function(){
    getContent('bodyPanel', 'LogOn/getLogOnPage');
};
function updateContent(updatePanel, relativeURL, parameters, additionalParameters)
{
    /*$.ajax({
        method: "POST",
        url: "http://localhost:8080/Shop-war/webresources/Test",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({ "name": "Stephen", "location": "Limerick"})
              //"{\"html\" : \"<h1 align=\\\"center\\\">TEST</h1>\"}"
    }).done(function(message){
        alert('Data Saved: ' + message);
    });*/
    var jsonArray = {};
    if(parameters && parameters.length > 0)
    {
        for(var counter = 0; counter < parameters.length; counter++)
        {
            if(document.getElementById(parameters[counter]))
            {
                jsonArray[parameters[counter]] = document.getElementById(parameters[counter]).value;
            }
            else
                jsonArray[parameters[counter]] = '';
        }
    }
    if(additionalParameters && additionalParameters.length > 0)
    {
        for(var counter = 0; counter < additionalParameters.length; counter = counter + 2)
        {
            if(additionalParameters[counter] && additionalParameters[counter + 1])
                jsonArray[additionalParameters[counter]] = additionalParameters[counter + 1];
        }
    }
    jsonArray = JSON.stringify(jsonArray);
    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: parentURL + relativeURL,
        dataType: "json",
        data: jsonArray,
        success: function(message, textStatus, jqXHR){
            //alert('DATA SUCCESSFULLY RECEIVED: ' + message['html']);
            //alert('MESSAGE: ' + message['html']);
            //alert('MESSAGE: ' + message['html']);
            if(message['html'])
                document.getElementById(updatePanel).innerHTML = message['html'];
            if(message['notification'] && message['notification'] === 'success')
                displaySuccessNotification(message['title'], message['description']);
            if(message['notification'] && message['notification'] === 'error')
                displayErrorNotification(message['title'], message['description']);
            if(message['table'])
                $('#' + message['table']).dataTable();
            //alert('HTML: ' + document.getElementById('bodyPanel').innerHTML);
        },
        error: function(jqXHR, textStatus, errorThrown){
            displayErrorNotification("Transport Error", textStatus);
        }
    });
}
/*
$.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: rootURL,
        dataType: "json",
        data: formToJSON(),
        success: function(data, textStatus, jqXHR){
            alert('Wine created successfully');
            $('#btnDelete').show();
            $('#wineId').val(data.id);
        },
        error: function(jqXHR, textStatus, errorThrown){
            alert('addWine error: ' + textStatus);
        }
    });
 */
/*function getContent()
{
    $.ajax({
        method: "GET",
        url: "http://localhost:8080/Shop-war/webresources/Test"
    }).done(function(message){
       alert('Message: ' + message);
       document.getElementById('bodyPanel').innerHTML = message['html'];
       if(message['table'])
           $('#' + message['table']).dataTable();
    });
}*/
function getContent(updatePanel, relativeURL)
{
    $.ajax({
        type: 'GET',
        url: parentURL + relativeURL,
        dataType: "json",
        success: function(message, textStatus, jqXHR){
            document.getElementById(updatePanel).innerHTML = message['html'];
            if(message['notification'] && message['notification'] === 'success')
                displaySuccessNotification(message['title'], message['description']);
            if(message['notification'] && message['notification'] === 'error')
                displayErrorNotification(message['title'], message['description']);
            if(message['table'])
                $('#' + message['table']).dataTable();
        },
        error: function(jqXHR, textStatus, errorThrown){
            displayErrorNotification("Transport Error", textStatus);
        }
    });
}
/*
$.ajax({
        type: 'GET',
        url: rootURL + '/' + id,
        dataType: "json",
        success: function(data){
            $('#btnDelete').show();
            renderDetails(data);
        }
    });
 */
function displaySuccessNotification(title, description)
{
    $.Notify({type: 'success', caption: title, content: description, icon: "<span class='mif-checkmark'></span>"});
}
function displayErrorNotification(title, description)
{
    $.Notify({type: 'alert', caption: title, content: description, icon: "<span class='mif-cross'></span>"});
}
function toggleTile(id)
{
    alert('TOGGLE TILE ID: ' + id);
    alert('TOGGLE TILE VALUE: ' + document.getElementById(id).value);
    if(document.getElementById(id))
    {
        var tileClassName = document.getElementById(id + "_Tile").className;
        if(document.getElementById(id).value === "on")
        {
            document.getElementById(id).value = "off";
            document.getElementById(id + '_Tile').className = tileClassName.substring(0, tileClassName.indexOf("element-selected"));
        }
        else
        {
            document.getElementById(id).value = "on";
            document.getElementById(id + '_Tile').className = tileClassName + " element-selected";
        }
    }
}
function selectTileInAccordion(id, titleText, titleIcon, selectedTileNumber)
{
    var accordion = document.getElementById(id);
    accordion.children[0].children[0].innerHTML = titleText + "<span class=\"mif-" + titleIcon + " icon\"></span>";
    var gridLayout = accordion.children[0].children[1].children[0].children[0];
    alert('GRID LAYOUT: ' + gridLayout.innerHTML);
    for(var counter = 0; counter < gridLayout.children.length; counter = counter + 3)
    {
        if(gridLayout.children[counter + 1])
        {
            var selectedTile = gridLayout.children[counter + 1].children[0];
            alert('SELECTED TILE: ' + selectedTile.innerHTML);
            if(selectedTile.className.indexOf("element-selected") > -1)
                toggleTile(selectedTile.id.substring(0, selectedTile.indexOf('_Tile')));
        }
    }
    if(gridLayout.children[(3 * (selectedTileNumber - 1)) + 1])
    {
        var selectedTileID = gridLayout.children[(3 * (selectedTileNumber - 1)) + 1].children[0].id;
        toggleTile(selectedTileID.substring(0, selectedTileID.indexOf('_Tile')));
    }
}
function selectItemInListView(id, value)
{
    if(document.getElementById(id))
        document.getElementById(id).value = value;
}
function toggleSwitch(id, titles)
{
    if(document.getElementById(id) && document.getElementById(id + '_HiddenInput') && 
    document.getElementById(id + '_HiddenInput').value === 'true')
    {
        var value = document.getElementById(id).value;
        var panelHeading = document.getElementById(id).parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.children[0].children[1];
        if(value === 'on')
        {
            var currentNode = document.getElementById(id);
            if(titles[1])
                panelHeading.innerHTML = titles[1];
            document.getElementById(id).value = 'off';
        }
        else
        {
            if(titles[0])
                panelHeading.innerHTML = titles[0];
            document.getElementById(id).value = 'on';
        }
        document.getElementById(id + '_HiddenInput').value = 'false';
    }
    else if(document.getElementById(id) && document.getElementById(id + '_HiddenInput'))
        document.getElementById(id + '_HiddenInput').value = 'true';
}
function hide(id)
{
    if(document.getElementById(id))
        $('#' + id).hide();
}
function show(id)
{
    if(document.getElementById(id))
        $('#' + id).show();
}
/*
*/
/*
$.ajax({
  method: "POST",
  url: "some.php",
  data: { name: "John", location: "Boston" }
})
  .done(function( msg ) {
    alert( "Data Saved: " + msg );
  });
 */


