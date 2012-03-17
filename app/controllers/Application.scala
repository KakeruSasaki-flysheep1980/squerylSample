package controllers

import play.api._
import play.api.mvc._
import org.squeryl.PrimitiveTypeMode

object Application extends Controller {

	def index = Action {
		import org.squeryl.PrimitiveTypeMode._
		import models.MyModel
		
		transaction {
			MyModel.drop
			MyModel.create	
			
			println("------------- insert")
			MyModel.myModel.insert(new MyModel("__name1__"))
			MyModel.myModel.insert(new MyModel("__name2__"))
			MyModel.myModel.insert(new MyModel("__name3__"))
			
			println("------------- findAll")
			MyModel.findAll.foreach(println)
		}
		
		val model1 = MyModel.findById(1L)
		transaction {
			println("------------- update")
			model1.get.updateName("__hoge__")
		}
		
		transaction {
			println("------------- findAll")
			MyModel.findAll.foreach(println)
		}
		
		Ok
	}
  
}