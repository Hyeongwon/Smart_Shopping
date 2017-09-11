/**
 * Created by byunhyeongwon on 2017. 9. 9..
 */
var mysql = require('mysql');

var database = {};
var pool;

database.init = function(app, config) {
    console.log('init() 호출됨.');

    connect(app, config);
};


function connect(app, config) {

    console.log('connect() 호출됨.');
    mysql.Promise = global.Promise;

    pool = mysql.createPool(config.db_info);

    database.db = pool;

    app.set('database', database);
    console.log('database 객체가 app 객체의 속성으로 추가됨.');
}

module.exports = database;