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

			println("========= insert")
			MyModel.myModel.insert(new MyModel("__name1__"))
			MyModel.myModel.insert(new MyModel("__name2__"))
			MyModel.myModel.insert(new MyModel("__name3__"))

			println("========= findAll")
			MyModel.findAll.foreach(println)
		}

		val model1 = MyModel.findById(1L)
		transaction {
			println("========= update")
			model1.get.updateName("__hoge__")
		}

		transaction {
			println("========= findAll")
			MyModel.findAll.foreach(println)
		}

		Ok
	}

	def rollbackTest = Action {
		import org.squeryl.PrimitiveTypeMode._
		import models.MyModel

		transaction {
			MyModel.drop
			MyModel.create

			println("========= insert")
			MyModel.myModel.insert(new MyModel("__name1__"))
			MyModel.myModel.insert(new MyModel("__name2__"))
			MyModel.myModel.insert(new MyModel("__name3__"))
			println("========= insert")

			println("========= findAll")
			MyModel.findAll.foreach(println)
			println("========= findAll")
		}

		val model1 = MyModel.findById(1L)
		try {
			transaction {
				println("========= update")
				model1.map(_.updateName("__hoge__"))
				MyModel.throwException
				println("========= update")
			}
		} catch {
			case e: Exception =>
		}

		transaction {
			println("========= findAll")
			MyModel.findAll.foreach(println)
			println("========= findAll")
		}

		Ok
	}

	def rollbackTest2 = MyAction { request =>
		println("========= update")
		val model1 = models.MyModel.findById(1L)
		model1.map(_.updateName("__hoge__"))
		models.MyModel.throwException
		println("========= update")

		Ok
	}

	private def MyAction(block: play.api.mvc.Request[AnyContent] => play.api.mvc.PlainResult): Action[AnyContent] = Action { request =>
		org.squeryl.PrimitiveTypeMode.transaction {
			import org.squeryl.PrimitiveTypeMode._
			import models.MyModel

			MyModel.drop
			MyModel.create

			println("========= insert")
			MyModel.myModel.insert(new MyModel("__name1__"))
			MyModel.myModel.insert(new MyModel("__name2__"))
			MyModel.myModel.insert(new MyModel("__name3__"))
			println("========= insert")

			println("========= findAll")
			MyModel.findAll.foreach(println)
			println("========= findAll")
		}

		try {
			org.squeryl.PrimitiveTypeMode.transaction {
				block(request)
			}
		} catch {
			case ex: Exception => {
				org.squeryl.PrimitiveTypeMode.transaction {
					println("========= findAll")
					models.MyModel.findAll.foreach(println)
					println("========= findAll")
				}

				BadRequest
			}
		}

	}
}
