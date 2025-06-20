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
          {{ t("components.konflikteLoesenForm.header.konflikteLoesen") }}
        </div>
      </v-col>
      <v-col md="3">
        <div
          v-if="konfliktloesenformdata.person_auskunftssperre.length > 0"
          class="text-h5 auskunftssperre"
        >
          {{ t("components.konflikteLoesenForm.header.auskunftssperre") }}
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
          {{ t("components.konflikteLoesenForm.buttons.alleUebernehmen") }}
        </v-btn>
        <v-btn
          :to="{ name: 'konflikte.index' }"
          class="ml-auto"
          exact
          variant="text"
        >
          {{ t("components.konflikteLoesenForm.buttons.abbrechen") }}
        </v-btn>
        <v-btn
          :disabled="konflikteVorhanden()"
          :loading="isSavingAnimation"
          color="green"
          @click="speichern"
        >
          {{ t("components.konflikteLoesenForm.buttons.speichern") }}
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col
        class="col"
        md="6"
      >
        <v-label class="text-h6">{{
          t("components.konflikteLoesenForm.header.aktuelleDaten")
        }}</v-label>
      </v-col>

      <v-col
        class="col"
        md="5"
      >
        <v-label class="text-h6">{{
          t("components.konflikteLoesenForm.header.neueDatenAusEWO")
        }}</v-label>
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
            :label="t('components.konflikteLoesenForm.form.familienname')"
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
            :label="t('components.konflikteLoesenForm.form.familienname')"
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
            :label="t('components.konflikteLoesenForm.form.geburtsname')"
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
            :label="t('components.konflikteLoesenForm.form.geburtsname')"
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
            :label="t('components.konflikteLoesenForm.form.vorname')"
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
            :label="t('components.konflikteLoesenForm.form.vorname')"
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
            :label="t('components.konflikteLoesenForm.form.geburtsdatum')"
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
            :label="t('components.konflikteLoesenForm.form.geburtsdatum')"
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
            :label="t('components.konflikteLoesenForm.form.geschlecht')"
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
            :label="t('components.konflikteLoesenForm.form.geschlecht')"
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
            :label="t('components.konflikteLoesenForm.form.ordnungsmerkmal')"
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
            :label="t('components.konflikteLoesenForm.form.ordnungsmerkmal')"
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
            :label="t('components.konflikteLoesenForm.form.akademischerGrad')"
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
            :label="t('components.konflikteLoesenForm.form.akademischerGrad')"
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
            :label="t('components.konflikteLoesenForm.form.geburtsOrt')"
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
            :label="t('components.konflikteLoesenForm.form.geburtsOrt')"
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
            :label="t('components.konflikteLoesenForm.form.geburtsLand')"
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
            :label="t('components.konflikteLoesenForm.form.geburtsLand')"
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
            :label="t('components.konflikteLoesenForm.form.familienStand')"
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
            :label="t('components.konflikteLoesenForm.form.familienStand')"
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
            :label="
              t('components.konflikteLoesenForm.form.staatsangehoerigkeit')
            "
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
            :label="
              t('components.konflikteLoesenForm.form.staatsangehoerigkeit')
            "
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
            :label="t('components.konflikteLoesenForm.form.wohnungsgeber')"
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
            :label="t('components.konflikteLoesenForm.form.wohnungsgeber')"
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
            :label="t('components.konflikteLoesenForm.form.strasse')"
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
            :label="t('components.konflikteLoesenForm.form.strasse')"
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
            :label="t('components.konflikteLoesenForm.form.hausnummer')"
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
            :label="t('components.konflikteLoesenForm.form.hausnummer')"
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
            :label="t('components.konflikteLoesenForm.form.appartmentNummer')"
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
            :label="t('components.konflikteLoesenForm.form.appartmentNummer')"
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
            :label="
              t('components.konflikteLoesenForm.form.buchstabeHausnummer')
            "
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
            :label="
              t('components.konflikteLoesenForm.form.buchstabeHausnummer')
            "
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
            :label="t('components.konflikteLoesenForm.form.stockwerk')"
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
            :label="t('components.konflikteLoesenForm.form.stockwerk')"
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
            :label="
              t('components.konflikteLoesenForm.form.teilnummerHausnummer')
            "
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
            :label="
              t('components.konflikteLoesenForm.form.teilnummerHausnummer')
            "
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
            :label="t('components.konflikteLoesenForm.form.adressZusatz')"
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
            :label="t('components.konflikteLoesenForm.form.adressZusatz')"
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
            :label="t('components.konflikteLoesenForm.form.postleitzahl')"
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
            :label="t('components.konflikteLoesenForm.form.postleitzahl')"
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
            :label="t('components.konflikteLoesenForm.form.ort')"
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
            :label="t('components.konflikteLoesenForm.form.ort')"
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
            :label="
              t('components.konflikteLoesenForm.form.inMuenchenWohnhaftSeit')
            "
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
            :label="
              t('components.konflikteLoesenForm.form.inMuenchenWohnhaftSeit')
            "
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
            :items="WOHNUNGSSTATUS_ARTEN"
            density="compact"
            :label="t('components.konflikteLoesenForm.form.wohnungsstatus')"
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
            :items="WOHNUNGSSTATUS_ARTEN"
            density="compact"
            :label="t('components.konflikteLoesenForm.form.wohnungsstatus')"
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
            :label="t('components.konflikteLoesenForm.form.auskunftsSperre')"
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
            :label="t('components.konflikteLoesenForm.form.auskunftsSperre')"
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
            :label="t('components.konflikteLoesenForm.form.derzeitigerBeruf')"
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
            :label="t('components.konflikteLoesenForm.form.arbeitgeber')"
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
            :label="
              t('components.konflikteLoesenForm.form.telefonnummerPrivat')
            "
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
            :label="
              t('components.konflikteLoesenForm.form.telefonnummerDienstlich')
            "
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
            :label="t('components.konflikteLoesenForm.form.telefonnummerMobil')"
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
            :label="t('components.konflikteLoesenForm.form.mailAdresse')"
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
            :label="
              t('components.konflikteLoesenForm.form.ausgeuebteEhrenaemter')
            "
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
            :label="t('components.konflikteLoesenForm.form.onlineBewerbung')"
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
            :label="t('components.konflikteLoesenForm.form.neuerVorschlag')"
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
            :label="
              t(
                'components.konflikteLoesenForm.form.warBereitsAlsVerwaltungsrichterTaetig',
                {
                  artEhrenamtJustiz: formattedEhrenamtjustizart(
                    useGlobalSettingsStore().getKonfiguration?.ehrenamtjustizart
                  ),
                }
              )
            "
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
            :label="
              t(
                'components.konflikteLoesenForm.form.warBereitsInVorperiodeAlsVerwaltungsrichterTaetig',
                {
                  artEhrenamtJustiz: formattedEhrenamtjustizart(
                    useGlobalSettingsStore().getKonfiguration?.ehrenamtjustizart
                  ),
                }
              )
            "
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
            :label="t('components.konflikteLoesenForm.form.bewerbungVom')"
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
            :label="t('components.konflikteLoesenForm.form.status')"
            persistent-placeholder
            variant="outlined"
          />
        </v-col>
      </v-row>
    </div>
  </v-form>
  <online-help-dialog-component
    :helptext="t('components.konflikteLoesenForm.onlineHelp')"
  />
</template>

<script lang="ts" setup>
import type KonfliktLoesenFormData from "@/types/KonfliktLoesenFormData";

import { mdiTransferLeft } from "@mdi/js";
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
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
import { STATUS_INDICATORS, WOHNUNGSSTATUS_ARTEN } from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";
import { formattedEhrenamtjustizart } from "@/tools/Helper.ts";

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
const KEINEEWODATEN =
  "Keine EWO-Daten gefunden. Evtl. verstorben oder verzogen?"; // Don't modify this text!!!
const { t } = useI18n();

onMounted(() => {
  if (isKonflikt(KEINEEWODATEN)) {
    snackbarStore.showMessage({
      level: STATUS_INDICATORS.WARNING,
      message: t("components.konflikteLoesenForm.form.messages.keineEWODaten"),
      show: true,
    });
  }
});

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
        message: t(
          "components.konflikteLoesenForm.form.messages.fehlerhafteEingabe"
        ),
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
  konfliktLoesenKeineEwoDaten();
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

function konfliktLoesenKeineEwoDaten() {
  if (!isKonflikt(KEINEEWODATEN)) return;
  removeKonflikt(KEINEEWODATEN);
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
