
var mysql = require("mysql");

/*
*    Creates a new connection to the database.
*    @param {function} callback - Callback used to obtain the connection.
*/
module.exports = {
    openConnection: function(callback) {
        // database credentials
        // var connection = mysql.openConnection({
        //     "host": "localhost",
        //     "user": "COMP4302jdavis17",
        //     "password": "YTM4NTM4YWNmYzA4Y2Nm",
        //     "database": "COMP4302jdavis17"
        // });
        var connection = mysql.createConnection({
            "host": "localhost",
            "user": "root",
            "password": "password",
            "database": "RSN"
        });

        // connect using the credentials above
        connection.connect(function(err) {
            // if there is a connection error, pass an error
            if (err) {
                callback(err, null);
            }
            // otherwise, pass a connection
            else {
                callback(null, connection);
            }
        });
    },

    /*
    *    Destroys an existing connection to the database.
    *    @param {Connection} connection - A database connection.
    */
    closeConnection: function(connection) {
        connection.destroy();
    }
};
