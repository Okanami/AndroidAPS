package app.aaps.plugins.automation.actions

import app.aaps.data.db.GlucoseUnit
import app.aaps.core.interfaces.queue.Callback
import app.aaps.database.impl.transactions.InsertIfNewByTimestampTherapyEventTransaction
import app.aaps.database.impl.transactions.Transaction
import app.aaps.plugins.automation.elements.InputCarePortalMenu
import app.aaps.plugins.automation.elements.InputDuration
import app.aaps.plugins.automation.elements.InputString
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`

class ActionCarePortalEventTest : ActionsTestBase() {

    private lateinit var sut: ActionCarePortalEvent

    @BeforeEach
    fun setup() {
        `when`(sp.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("AAPS")
        `when`(rh.gs(app.aaps.core.ui.R.string.careportal_note_message)).thenReturn("Note : %s")
        `when`(dateUtil.now()).thenReturn(0)
        `when`(profileFunction.getUnits()).thenReturn(GlucoseUnit.MGDL)
        `when`(repository.runTransactionForResult(anyObject<Transaction<InsertIfNewByTimestampTherapyEventTransaction.TransactionResult>>()))
            .thenReturn(Single.just(InsertIfNewByTimestampTherapyEventTransaction.TransactionResult().apply {
            }))
        sut = ActionCarePortalEvent(injector)
        sut.cpEvent = InputCarePortalMenu(rh)
        sut.cpEvent.value = InputCarePortalMenu.EventType.NOTE
        sut.note = InputString("Asd")
        sut.duration = InputDuration(5, InputDuration.TimeUnit.MINUTES)
    }

    @Test fun friendlyNameTest() {
        Assertions.assertEquals(app.aaps.core.ui.R.string.careportal, sut.friendlyName())
    }

    @Test fun shortDescriptionTest() {
        Assertions.assertEquals("Note : Asd", sut.shortDescription())
    }

    @Test fun iconTest() {
        Assertions.assertEquals(app.aaps.core.main.R.drawable.ic_cp_note, sut.icon())
    }

    @Test fun doActionTest() {
        sut.doAction(object : Callback() {
            override fun run() {
                Assertions.assertTrue(result.success)
            }
        })
    }

    @Test fun hasDialogTest() {
        Assertions.assertTrue(sut.hasDialog())
    }

    @Test fun toJSONTest() {
        Assertions.assertEquals(
            "{\"data\":{\"note\":\"Asd\",\"cpEvent\":\"NOTE\",\"durationInMinutes\":5},\"type\":\"ActionCarePortalEvent\"}",
            sut.toJSON()
        )
    }

    @Test fun fromJSONTest() {
        sut.note = InputString("Asd")
        sut.fromJSON("{\"note\":\"Asd\",\"cpEvent\":\"NOTE\",\"durationInMinutes\":5}")
        Assertions.assertEquals("Asd", sut.note.value)
        Assertions.assertEquals(5, sut.duration.value)
        Assertions.assertEquals(InputCarePortalMenu.EventType.NOTE, sut.cpEvent.value)
    }
}