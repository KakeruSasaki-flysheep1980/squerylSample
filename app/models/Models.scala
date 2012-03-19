package models

import org.squeryl.KeyedEntity
import java.sql.Timestamp
import org.squeryl.Schema
import org.squeryl.Table
import java.util.Date
import org.squeryl.PrimitiveTypeMode._

case class MyModel(val id: Long, var name: String, var createTime: Timestamp, var updateTime: Timestamp) extends BaseModel {
	def this(name: String) = this(0L, name, new Timestamp(0L), new Timestamp(0L))
	
	def updateName(sname: String) = {
		this.name_=(sname)
		this.update
	}
}

trait BaseModel extends KeyedEntity[Long] {
	val id: Long
	var createTime: Timestamp
	var updateTime: Timestamp
}

object MyModel extends MySchema[MyModel] {

	val myModel = table[MyModel]
	override val classManifest = manifest[MyModel]
	
	def findAll: List[MyModel] = {
		inTransaction {
			from(myModel)(t => select(t)).toList
		}
	}
	
	def findById(id: Long): Option[MyModel] = {
		inTransaction {
			from(myModel)(t => where(t.id === id) select(t)).headOption
		}
	}

}

trait MySchema[T <: BaseModel] extends Schema {

	implicit val classManifest: scala.reflect.Manifest[T]
	override def callbacks = {
			Seq(
					beforeInsert[T].map(p => {
						p.createTime_=(new Timestamp(new Date().getTime))
						p.updateTime_=(new Timestamp(new Date().getTime))
						p
					}),
					beforeUpdate[T]map(p => {
						p.updateTime_=(new Timestamp(new Date().getTime))
						p
					})
			)
	}

}