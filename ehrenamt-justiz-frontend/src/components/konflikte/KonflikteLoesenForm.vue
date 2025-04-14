<template>
  <v-form
    ref="form"
    :model-value="formValid"
    class="form"
    :readonly="true"
    @submit="speichern"
    @keydown.enter.prevent="speichern"
  >
    <v-row>
      <v-col
        class="text-left"
        md="4"
      >
        <div
          class="text-h5"
          style="margin-bottom: 1em"
        >
          Konflikte lösen
        </div>
      </v-col>
      <v-col md="3">
        <div
          v-if="konfliktloesenformdata.person_auskunftssperre.length > 0"
          class="text-h5 auskunftssperre"
        >
          Auskunftssperre
        </div>
      </v-col>
      <v-col
        class="text-right"
        md="5"
      >
        <v-btn
          :disabled="!konflikteVorhanden()"
          color="accent"
          @click="alleUebernehmen"
        >
          Alle übernehmen
        </v-btn>
        <v-btn
          :to="{ name: 'konflikte.index' }"
          class="ml-auto"
          exact
          variant="text"
        >
          Abbrechen
        </v-btn>
        <v-btn
          :disabled="konflikteVorhanden()"
          :loading="isSavingAnimation"
          color="green"
          @click="speichern"
        >
          Speichern
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col
        class="col"
        md="6"
      >
        <v-label class="text-h6">Aktuelle Daten</v-label>
      </v-col>

      <v-col
        class="col"
        md="5"
      >
        <v-label class="text-h6">Neue Daten aus EWO</v-label>
      </v-col>
    </v-row>
    <div
      style="overflow-y: scroll; overflow-x: hidden"
      class="div"
    >
      <br />
      <!-- As the label of the first field is only partially displayed -->
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_familienname"
            density="compact"
            :label="FAMILIENNAME"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(FAMILIENNAME)"
            :disabled="!isKonflikt(FAMILIENNAME)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenFamilienname()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(FAMILIENNAME)"
            v-model="konfliktloesenformdata.ewo_familienname"
            density="compact"
            :label="FAMILIENNAME"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_geburtsname"
            density="compact"
            :label="GEBURTSNAME"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(GEBURTSNAME)"
            :disabled="!isKonflikt(GEBURTSNAME)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenGeburtsname()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(GEBURTSNAME)"
            v-model="konfliktloesenformdata.ewo_geburtsname"
            density="compact"
            :label="GEBURTSNAME"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_vorname"
            density="compact"
            :label="VORNAME"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(VORNAME)"
            :disabled="!isKonflikt(VORNAME)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenVorname()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(VORNAME)"
            v-model="konfliktloesenformdata.ewo_vorname"
            density="compact"
            :label="VORNAME"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_geburtsdatum"
            density="compact"
            :label="GEBURTSDATUM"
            persistent-placeholder
            type="date"
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(GEBURTSDATUM)"
            :disabled="!isKonflikt(GEBURTSDATUM)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenGeburtsdatum()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(GEBURTSDATUM)"
            v-model="konfliktloesenformdata.ewo_geburtsdatum"
            density="compact"
            :label="GEBURTSDATUM"
            persistent-placeholder
            type="date"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-select
            v-model="konfliktloesenformdata.person_geschlecht"
            :items="BewerbungForm.geschlechtswerte"
            density="compact"
            :label="GESCHLECHT"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(GESCHLECHT)"
            :disabled="!isKonflikt(GESCHLECHT)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenGeschlecht()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-select
            v-if="isKonfliktOriginal(GESCHLECHT)"
            v-model="konfliktloesenformdata.ewo_geschlecht"
            :items="BewerbungForm.geschlechtswerte"
            density="compact"
            :label="GESCHLECHT"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_ewoid"
            density="compact"
            :label="ORDNUNGSMERKMAL"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(ORDNUNGSMERKMAL)"
            :disabled="!isKonflikt(ORDNUNGSMERKMAL)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenOrdnungsmerkmal()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(ORDNUNGSMERKMAL)"
            v-model="konfliktloesenformdata.ewo_ewoid"
            density="compact"
            :label="ORDNUNGSMERKMAL"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_akademischergrad"
            density="compact"
            :label="AKADEMISCHERGRAD"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(AKADEMISCHERGRAD)"
            :disabled="!isKonflikt(AKADEMISCHERGRAD)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenAkademischergrad()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(AKADEMISCHERGRAD)"
            v-model="konfliktloesenformdata.ewo_akademischergrad"
            density="compact"
            :label="AKADEMISCHERGRAD"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_geburtsort"
            density="compact"
            :label="GEBURTSORT"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(GEBURTSORT)"
            :disabled="!isKonflikt(GEBURTSORT)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenGeburtsort()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(GEBURTSORT)"
            v-model="konfliktloesenformdata.ewo_geburtsort"
            density="compact"
            :label="GEBURTSORT"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_geburtsland"
            density="compact"
            :label="GEBURTSLAND"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(GEBURTSLAND)"
            :disabled="!isKonflikt(GEBURTSLAND)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenGeburtsland()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(GEBURTSLAND)"
            v-model="konfliktloesenformdata.ewo_geburtsland"
            density="compact"
            :label="GEBURTSLAND"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_familienstand"
            density="compact"
            :label="FAMILIENSTAND"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(FAMILIENSTAND)"
            :disabled="!isKonflikt(FAMILIENSTAND)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenFamilienstand()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(FAMILIENSTAND)"
            v-model="konfliktloesenformdata.ewo_familienstand"
            density="compact"
            :label="FAMILIENSTAND"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_staatsangehoerigkeit"
            density="compact"
            :label="STAATSANGEHOERIGKEIT"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(STAATSANGEHOERIGKEIT)"
            :disabled="!isKonflikt(STAATSANGEHOERIGKEIT)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenStaatsangehoerigkeiten()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(STAATSANGEHOERIGKEIT)"
            v-model="konfliktloesenformdata.ewo_staatsangehoerigkeit"
            density="compact"
            :label="STAATSANGEHOERIGKEIT"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_wohnungsgeber"
            density="compact"
            :label="WOHNUNGSGEBER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(WOHNUNGSGEBER)"
            :disabled="!isKonflikt(WOHNUNGSGEBER)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenWohnungsgeber()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(WOHNUNGSGEBER)"
            v-model="konfliktloesenformdata.ewo_wohnungsgeber"
            density="compact"
            :label="WOHNUNGSGEBER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_strasse"
            density="compact"
            :label="STRASSE"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(STRASSE)"
            :disabled="!isKonflikt(STRASSE)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenStrasse()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(STRASSE)"
            v-model="konfliktloesenformdata.ewo_strasse"
            density="compact"
            :label="STRASSE"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_hausnummer"
            density="compact"
            :label="HAUSNUMMER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(HAUSNUMMER)"
            :disabled="!isKonflikt(HAUSNUMMER)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenHausnummer()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(HAUSNUMMER)"
            v-model="konfliktloesenformdata.ewo_hausnummer"
            density="compact"
            :label="HAUSNUMMER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_appartmentnummer"
            density="compact"
            :label="APPARTMENTNUMMER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(APPARTMENTNUMMER)"
            :disabled="!isKonflikt(APPARTMENTNUMMER)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenAppartmentnummer()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(APPARTMENTNUMMER)"
            v-model="konfliktloesenformdata.ewo_appartmentnummer"
            density="compact"
            :label="APPARTMENTNUMMER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_buchstabehausnummer"
            density="compact"
            :label="BUCHSTABEHAUSNUMMER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(BUCHSTABEHAUSNUMMER)"
            :disabled="!isKonflikt(BUCHSTABEHAUSNUMMER)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenBuchstabehausnummer()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(BUCHSTABEHAUSNUMMER)"
            v-model="konfliktloesenformdata.ewo_buchstabehausnummer"
            density="compact"
            :label="BUCHSTABEHAUSNUMMER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_stockwerk"
            density="compact"
            :label="STOCKWERK"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(STOCKWERK)"
            :disabled="!isKonflikt(STOCKWERK)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenStockwerk()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(STOCKWERK)"
            v-model="konfliktloesenformdata.ewo_stockwerk"
            density="compact"
            :label="STOCKWERK"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_teilnummerhausnummer"
            density="compact"
            :label="TEILNUMMERHAUSNUMMER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(TEILNUMMERHAUSNUMMER)"
            :disabled="!isKonflikt(TEILNUMMERHAUSNUMMER)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenTeilnummerhausnummer()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(TEILNUMMERHAUSNUMMER)"
            v-model="konfliktloesenformdata.ewo_teilnummerhausnummer"
            density="compact"
            :label="TEILNUMMERHAUSNUMMER"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_adresszusatz"
            density="compact"
            :label="ADRESSZUSATZ"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(ADRESSZUSATZ)"
            :disabled="!isKonflikt(ADRESSZUSATZ)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenAdresszusatz()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(ADRESSZUSATZ)"
            v-model="konfliktloesenformdata.ewo_adresszusatz"
            density="compact"
            :label="ADRESSZUSATZ"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_postleitzahl"
            density="compact"
            :label="POSTLEITZAHL"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(POSTLEITZAHL)"
            :disabled="!isKonflikt(POSTLEITZAHL)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenPostleitzahl()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(POSTLEITZAHL)"
            v-model="konfliktloesenformdata.ewo_postleitzahl"
            density="compact"
            :label="POSTLEITZAHL"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_ort"
            density="compact"
            :label="ORT"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(ORT)"
            :disabled="!isKonflikt(ORT)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenOrt()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(ORT)"
            v-model="konfliktloesenformdata.ewo_ort"
            density="compact"
            :label="ORT"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_inmuenchenseit"
            density="compact"
            label="In Muenchen seit"
            persistent-placeholder
            type="date"
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(INMUENCHENSEIT)"
            :disabled="!isKonflikt(INMUENCHENSEIT)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenInMuenchenseit()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-if="isKonfliktOriginal(INMUENCHENSEIT)"
            v-model="konfliktloesenformdata.ewo_inmuenchenseit"
            density="compact"
            label="In Muenchen seit"
            persistent-placeholder
            type="date"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-select
            v-model="konfliktloesenformdata.person_wohnungsstatus"
            :items="wohnungsstatus"
            density="compact"
            :label="WOHNUNGSSTATUS"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(WOHNUNGSSTATUS)"
            :disabled="!isKonflikt(WOHNUNGSSTATUS)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenWohnungsstatus()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-select
            v-if="isKonfliktOriginal(WOHNUNGSSTATUS)"
            v-model="konfliktloesenformdata.ewo_wohnungsstatus"
            :items="wohnungsstatus"
            density="compact"
            :label="WOHNUNGSSTATUS"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-textarea
            v-model="konfliktloesenformdata.person_auskunftssperre"
            density="compact"
            :label="AUSKUNFTSSPERRE"
            persistent-placeholder
            no-resize
            rows="2"
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          md="1"
        >
          <v-btn
            v-if="isKonfliktOriginal(AUSKUNFTSSPERRE)"
            :disabled="!isKonflikt(AUSKUNFTSSPERRE)"
            class="button-konflikt-loesen"
            @click="konfliktLoesenAuskunftssperren()"
          >
            <v-icon :icon="mdiTransferLeft" />
          </v-btn>
        </v-col>
        <v-col
          class="col"
          md="5"
        >
          <v-textarea
            v-if="isKonfliktOriginal(AUSKUNFTSSPERRE)"
            v-model="konfliktloesenformdata.ewo_auskunftssperre"
            density="compact"
            :label="AUSKUNFTSSPERRE"
            persistent-placeholder
            no-resize
            rows="2"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_derzeitausgeuebterberuf"
            density="compact"
            label="Derzeitiger Beruf"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_arbeitgeber"
            density="compact"
            label="Arbeitgeber"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_telefonnummer"
            density="compact"
            label="Telefonnummer (privat)"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_telefongesch"
            density="compact"
            label="Telefonnummer (dienstlich)"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_telefonmobil"
            density="compact"
            label="Telefonnummer (mobil)"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_mailadresse"
            density="compact"
            label="Mailadresse"
            persistent-placeholder
            type="mail"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_ausgeuebteehrenaemter"
            density="compact"
            label="Ausgeübte Ehrenämter"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_onlinebewerbung"
            density="compact"
            label="Online Bewerbung?"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_neuervorschlag"
            density="compact"
            label="Neuer Vorschlag?"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-checkbox
            v-model="konfliktloesenformdata.person_warbereitstaetigals"
            class="ma-6"
            density="compact"
            :label="labelWarBereitsTaetigAls"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-checkbox
            v-model="
              konfliktloesenformdata.person_warbereitstaetigalsvorvorperiode
            "
            class="ma-6"
            density="compact"
            :label="labelWarBereitsTaetigAlsVorVorPeriode"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-text-field
            v-model="konfliktloesenformdata.person_bewerbungvom"
            density="compact"
            label="Bewerbung vom"
            persistent-placeholder
            type="date"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          md="5"
        >
          <v-select
            v-model="konfliktloesenformdata.person_status"
            :items="BewerbungForm.statuswerte"
            density="compact"
            label="Status"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
    </div>
  </v-form>
  <online-help-dialog-component
    component="Das ist die Onlinehilfe für die Bearbeitung der Konflikte (Under Construction)"
  />
</template>

<script lang="ts" setup>
import type KonfliktLoesenFormData from "@/types/KonfliktLoesenFormData";

import { mdiTransferLeft } from "@mdi/js";
import { computed, ref } from "vue";
import {
  VBtn,
  VCheckbox,
  VCol,
  VForm,
  VIcon,
  VLabel,
  VRow,
  VSelect,
  VTextarea,
  VTextField,
} from "vuetify/components";

import BewerbungForm from "@/components/bewerbungen/BewerbungForm.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";

const props = defineProps<{
  modelValue: KonfliktLoesenFormData;
  isSavingAnimation: boolean;
}>();
const emits = defineEmits<{
  "update:modelValue": [v: KonfliktLoesenFormData];
  save: [];
}>();
const snackbarStore = useSnackbarStore();
const form = ref();

const formValid = ref(false);

const wohnungsstatus: string[] = ["Hauptwohnung", "Nebenwohnung"];

const FAMILIENNAME = "Familienname";
const GEBURTSNAME = "Geburtsname";
const VORNAME = "Vorname";
const GEBURTSDATUM = "Geburtsdatum";
const GESCHLECHT = "Geschlecht";
const ORDNUNGSMERKMAL = "Ordnungsmerkmal";
const AKADEMISCHERGRAD = "Akademischergrad";
const GEBURTSORT = "Geburtsort";
const GEBURTSLAND = "Geburtsland";
const FAMILIENSTAND = "Familienstand";
const STAATSANGEHOERIGKEIT = "Staatsangehoerigkeit";
const WOHNUNGSGEBER = "Wohnungsgeber";
const STRASSE = "Strasse";
const HAUSNUMMER = "Hausnummer";
const APPARTMENTNUMMER = "Appartmentnummer";
const BUCHSTABEHAUSNUMMER = "Buchstabehausnummer";
const STOCKWERK = "Stockwerk";
const TEILNUMMERHAUSNUMMER = "Teilnummerhausnummer";
const ADRESSZUSATZ = "Adresszusatz";
const POSTLEITZAHL = "Postleitzahl";
const ORT = "Ort";
const INMUENCHENSEIT = "Inmuenchenseit";
const WOHNUNGSSTATUS = "Wohnungsstatus";
const AUSKUNFTSSPERRE = "Auskunftssperre";

const labelWarBereitsTaetigAls = ref(
  "War bereits als " +
    useGlobalSettingsStore().getKonfiguration?.ehrenamtjustizart +
    " tätig"
);

const labelWarBereitsTaetigAlsVorVorPeriode = ref(
  "War bereits in Vorvorperiode als " +
    useGlobalSettingsStore().getKonfiguration?.ehrenamtjustizart +
    " tätig"
);

const konfliktloesenformdata = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});

const konfliktfeld: string[] =
  konfliktloesenformdata.value.person_konfliktfeld.slice();

function speichern(): void {
  if (konflikteVorhanden()) {
    return;
  }

  form.value?.validate().then((validation: { valid: boolean }) => {
    if (!validation.valid) {
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.WARNING,
        message: "Das Formular ist nicht richtig ausgefüllt.",
        show: true,
      });
    } else {
      emits("save");
    }
  });
}

function isKonflikt(feldname: string): boolean {
  return (
    konfliktloesenformdata.value.person_konfliktfeld.indexOf(feldname) > -1
  );
}

function isKonfliktOriginal(feldname: string): boolean {
  return konfliktfeld.indexOf(feldname) > -1;
}

function removeKonflikt(feldname: string): void {
  const index = konfliktloesenformdata.value.person_konfliktfeld.indexOf(
    feldname,
    0
  );
  if (index > -1) {
    konfliktloesenformdata.value.person_konfliktfeld.splice(index, 1);
  }
}

// Resolve all conflicts
function alleUebernehmen(): void {
  konfliktLoesenFamilienname();
  konfliktLoesenGeburtsname();
  konfliktLoesenVorname();
  konfliktLoesenGeburtsdatum();
  konfliktLoesenGeschlecht();
  konfliktLoesenOrdnungsmerkmal();
  konfliktLoesenAkademischergrad();
  konfliktLoesenGeburtsort();
  konfliktLoesenGeburtsland();
  konfliktLoesenFamilienstand();
  konfliktLoesenStaatsangehoerigkeiten();
  konfliktLoesenWohnungsgeber();
  konfliktLoesenStrasse();
  konfliktLoesenHausnummer();
  konfliktLoesenAppartmentnummer();
  konfliktLoesenBuchstabehausnummer();
  konfliktLoesenStockwerk();
  konfliktLoesenTeilnummerhausnummer();
  konfliktLoesenAdresszusatz();
  konfliktLoesenPostleitzahl();
  konfliktLoesenOrt();
  konfliktLoesenInMuenchenseit();
  konfliktLoesenWohnungsstatus();
  konfliktLoesenAuskunftssperren();
}

// Resolve single conflict
function konfliktLoesenFamilienname() {
  if (!isKonflikt(FAMILIENNAME)) return;
  konfliktloesenformdata.value["person_familienname"] =
    konfliktloesenformdata.value["ewo_familienname"];
  removeKonflikt(FAMILIENNAME);
}

function konfliktLoesenGeburtsname() {
  if (!isKonflikt(GEBURTSNAME)) return;
  konfliktloesenformdata.value["person_geburtsname"] =
    konfliktloesenformdata.value["ewo_geburtsname"];
  removeKonflikt(GEBURTSNAME);
}

function konfliktLoesenVorname() {
  if (!isKonflikt(VORNAME)) return;
  konfliktloesenformdata.value["person_vorname"] =
    konfliktloesenformdata.value["ewo_vorname"];
  removeKonflikt(VORNAME);
}

function konfliktLoesenGeburtsdatum() {
  if (!isKonflikt(GEBURTSDATUM)) return;
  konfliktloesenformdata.value["person_geburtsdatum"] =
    konfliktloesenformdata.value["ewo_geburtsdatum"];
  removeKonflikt(GEBURTSDATUM);
}

function konfliktLoesenGeschlecht() {
  if (!isKonflikt(GESCHLECHT)) return;
  konfliktloesenformdata.value["person_geschlecht"] =
    konfliktloesenformdata.value["ewo_geschlecht"];
  removeKonflikt(GESCHLECHT);
}

function konfliktLoesenOrdnungsmerkmal() {
  if (!isKonflikt(ORDNUNGSMERKMAL)) return;
  konfliktloesenformdata.value["person_ewoid"] =
    konfliktloesenformdata.value["ewo_ewoid"];
  removeKonflikt(ORDNUNGSMERKMAL);
}

function konfliktLoesenAkademischergrad() {
  if (!isKonflikt(AKADEMISCHERGRAD)) return;
  konfliktloesenformdata.value["person_akademischergrad"] =
    konfliktloesenformdata.value["ewo_akademischergrad"];
  removeKonflikt(AKADEMISCHERGRAD);
}

function konfliktLoesenGeburtsort() {
  if (!isKonflikt(GEBURTSORT)) return;
  konfliktloesenformdata.value["person_geburtsort"] =
    konfliktloesenformdata.value["ewo_geburtsort"];
  removeKonflikt(GEBURTSORT);
}

function konfliktLoesenGeburtsland() {
  if (!isKonflikt(GEBURTSLAND)) return;
  konfliktloesenformdata.value["person_geburtsland"] =
    konfliktloesenformdata.value["ewo_geburtsland"];
  removeKonflikt(GEBURTSLAND);
}

function konfliktLoesenFamilienstand() {
  if (!isKonflikt(FAMILIENSTAND)) return;
  konfliktloesenformdata.value["person_familienstand"] =
    konfliktloesenformdata.value["ewo_familienstand"];
  removeKonflikt(FAMILIENSTAND);
}

function konfliktLoesenStaatsangehoerigkeiten() {
  if (!isKonflikt(STAATSANGEHOERIGKEIT)) return;
  konfliktloesenformdata.value["person_staatsangehoerigkeit"] =
    konfliktloesenformdata.value["ewo_staatsangehoerigkeit"];
  removeKonflikt(STAATSANGEHOERIGKEIT);
}

function konfliktLoesenWohnungsgeber() {
  if (!isKonflikt(WOHNUNGSGEBER)) return;
  konfliktloesenformdata.value["person_wohnungsgeber"] =
    konfliktloesenformdata.value["ewo_wohnungsgeber"];
  removeKonflikt(WOHNUNGSGEBER);
}

function konfliktLoesenStrasse() {
  if (!isKonflikt(STRASSE)) return;
  konfliktloesenformdata.value["person_strasse"] =
    konfliktloesenformdata.value["ewo_strasse"];
  removeKonflikt(STRASSE);
}

function konfliktLoesenHausnummer() {
  if (!isKonflikt(HAUSNUMMER)) return;
  konfliktloesenformdata.value["person_hausnummer"] =
    konfliktloesenformdata.value["ewo_hausnummer"];
  removeKonflikt(HAUSNUMMER);
}

function konfliktLoesenAppartmentnummer() {
  if (!isKonflikt(APPARTMENTNUMMER)) return;
  konfliktloesenformdata.value["person_appartmentnummer"] =
    konfliktloesenformdata.value["ewo_appartmentnummer"];
  removeKonflikt(APPARTMENTNUMMER);
}

function konfliktLoesenBuchstabehausnummer() {
  if (!isKonflikt(BUCHSTABEHAUSNUMMER)) return;
  konfliktloesenformdata.value["person_buchstabehausnummer"] =
    konfliktloesenformdata.value["ewo_buchstabehausnummer"];
  removeKonflikt(BUCHSTABEHAUSNUMMER);
}

function konfliktLoesenStockwerk() {
  if (!isKonflikt(STOCKWERK)) return;
  konfliktloesenformdata.value["person_stockwerk"] =
    konfliktloesenformdata.value["ewo_stockwerk"];
  removeKonflikt(STOCKWERK);
}

function konfliktLoesenTeilnummerhausnummer() {
  if (!isKonflikt(TEILNUMMERHAUSNUMMER)) return;
  konfliktloesenformdata.value["person_teilnummerhausnummer"] =
    konfliktloesenformdata.value["ewo_teilnummerhausnummer"];
  removeKonflikt(TEILNUMMERHAUSNUMMER);
}

function konfliktLoesenAdresszusatz() {
  if (!isKonflikt(ADRESSZUSATZ)) return;
  konfliktloesenformdata.value["person_adresszusatz"] =
    konfliktloesenformdata.value["ewo_adresszusatz"];
  removeKonflikt(ADRESSZUSATZ);
}

function konfliktLoesenPostleitzahl() {
  if (!isKonflikt(POSTLEITZAHL)) return;
  konfliktloesenformdata.value["person_postleitzahl"] =
    konfliktloesenformdata.value["ewo_postleitzahl"];
  removeKonflikt(POSTLEITZAHL);
}

function konfliktLoesenOrt() {
  if (!isKonflikt(ORT)) return;
  konfliktloesenformdata.value["person_ort"] =
    konfliktloesenformdata.value["ewo_ort"];
  removeKonflikt(ORT);
}

function konfliktLoesenInMuenchenseit() {
  if (!isKonflikt(INMUENCHENSEIT)) return;
  konfliktloesenformdata.value["person_inmuenchenseit"] =
    konfliktloesenformdata.value["ewo_inmuenchenseit"];
  removeKonflikt(INMUENCHENSEIT);
}

function konfliktLoesenWohnungsstatus() {
  if (!isKonflikt(WOHNUNGSSTATUS)) return;
  konfliktloesenformdata.value["person_wohnungsstatus"] =
    konfliktloesenformdata.value["ewo_wohnungsstatus"];
  removeKonflikt(WOHNUNGSSTATUS);
}

function konfliktLoesenAuskunftssperren() {
  if (!isKonflikt(AUSKUNFTSSPERRE)) return;
  konfliktloesenformdata.value["person_auskunftssperre"] =
    konfliktloesenformdata.value["ewo_auskunftssperre"];
  removeKonflikt(AUSKUNFTSSPERRE);
}

function konflikteVorhanden(): boolean {
  return konfliktloesenformdata.value.person_konfliktfeld.length > 0;
}
</script>

<style scoped>
.button-konflikt-loesen {
  color: white;
  background-color: red;
}

.auskunftssperre {
  color: red;
}
</style>
