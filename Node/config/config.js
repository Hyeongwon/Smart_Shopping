/**
 * Created by byunhyeongwon on 2017. 9. 9..
 */
module.exports = {
    route_info: [
         {file:'./product', path:'/process/add_user', method:'addUser', type:'post'}
        ,{file:'./product', path:'/process/get_product_name', method:'getData', type:'post'}
        ,{file:'./product', path:'/process/get_product_name_json', method:'getJson', type:'post'}
        ,{file:'./product', path:'/images', method:'getImage', type:'get'}
        ,{file:'./login', path:'/process/login', method:'login', type:'post'}
    ],

    db_info :
    {
        connectionLimit : 10,
        host : 'localhost',
        user : 'root',
        password : 'qus900506' ,
        database : 'test',
        debug : false
    }

};