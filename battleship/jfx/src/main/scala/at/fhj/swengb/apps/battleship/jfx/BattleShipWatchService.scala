package at.fhj.swengb.apps.battleship.jfx

import java.io.IOException
import java.nio.file._
import java.util.concurrent.TimeUnit

import scala.collection.JavaConverters._
import javafx.application.Platform

class BattleShipWatchService extends Runnable{


  def printEvent(event:WatchEvent[_]) : Unit = {
    val kind = event.kind
    if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
      if (BattleShipFxApp.gameStateEdit) {
        BattleShipFxApp.getEditController.watcher()
      } else {
        BattleShipFxApp.getGameController.watcher()
      }
    }
  }

    def stop(): Unit = {
    BattleShipFxApp.watchCycle = false
  }

  override def run(): Unit = {
    Thread.sleep(1000)
    try {
      val path: Path = FileSystems.getDefault().getPath(BattleShipFxApp.getPlayPath)
      val watcher: WatchService = FileSystems.getDefault.newWatchService

      path.register(
        watcher,
         StandardWatchEventKinds.ENTRY_MODIFY

      )

      while(BattleShipFxApp.watchCycle) {
        val watchKey = watcher.take()
        watchKey.pollEvents().asScala.foreach(e => {
            printEvent(e)
          })
        if (!watchKey.reset()) {
          println("No longer valid")
        }
      }
    } catch {
      case ie: InterruptedException => println("InterruptedException: " + ie)
      case ioe: IOException => println("IOException: " + ioe)
      case e: Exception => println("Exception: " + e)
    }
  }
}
