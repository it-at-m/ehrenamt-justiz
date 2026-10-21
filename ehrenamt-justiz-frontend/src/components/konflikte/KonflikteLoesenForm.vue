<template>
  <v-form
    ref="form"
    class="form"
    :readonly="true"
    @submit.prevent="speichern"
  >
    <v-row>
      <v-col
        class="text-left"
        cols="4"
      >
        <div
          class="text-headline-small"
          style="margin-bottom: 1em"
        >
          {{ t("components.konflikteLoesenForm.header.konflikteLoesen") }}
        </div>
      </v-col>
      <v-col cols="3">
        <div
          v-if="konfliktloesenformdata.person_auskunftssperre.length > 0"
          class="text-headline-small auskunftssperreDetail"
        >
          {{ t("components.konflikteLoesenForm.header.auskunftssperre") }}
        </div>
      </v-col>
      <v-col
        class="text-right"
        cols="5"
      >
        <v-btn
          :to="{ name: '/konflikte/konflikteindex' }"
          class="ml-auto mr-2"
          data-test="konflikteloesenform-abbrechen-button"
          variant="text"
        >
          {{ t("components.konflikteLoesenForm.buttons.abbrechen") }}
        </v-btn>
        <v-btn
          :disabled="!konflikteVorhanden()"
          color="accent"
          class="mr-2"
          data-test="konflikteloesenform-alleuebernehmen-button"
          @click="alleUebernehmen"
        >
          {{ t("components.konflikteLoesenForm.buttons.alleUebernehmen") }}
        </v-btn>
        <v-btn
          :disabled="konflikteVorhanden()"
          :loading="isSavingAnimation"
          color="action"
          type="submit"
          data-test="konflikteloesenform-speichern-button"
        >
          {{ t("components.konflikteLoesenForm.buttons.speichern") }}
        </v-btn>
      </v-col>
    </v-row>

    <v-tabs v-model="active_tab">
      <v-tab
        value="bewerberZusatzangaben"
        data-test="konflikteloesenform-bewerberzusatzangaben-tab"
        >{{ t("components.konflikteLoesenForm.tab.bewerberZusatzangaben") }}
      </v-tab>
      <v-tab
        value="datenAusEWOUebernehmen"
        data-test="konflikteloesenform-datenausewouebernehmen-tab"
        >{{ t("components.konflikteLoesenForm.tab.datenAusEWOUebernehmen") }}
      </v-tab>
    </v-tabs>
    <v-tabs-window v-model="active_tab">
      <v-tabs-window-item
        style="padding: 1em"
        value="bewerberZusatzangaben"
        eager
      >
        <v-card>
          <v-card-text>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="
                    konfliktloesenformdata.person_derzeitausgeuebterberuf
                  "
                  density="compact"
                  :label="
                    t('components.konflikteLoesenForm.form.derzeitigerBeruf')
                  "
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-derzeitigerberuf-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_arbeitgeber"
                  density="compact"
                  :label="t('components.konflikteLoesenForm.form.arbeitgeber')"
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-arbeitgeber-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_telefonnummer"
                  density="compact"
                  :label="
                    t('components.konflikteLoesenForm.form.telefonnummerPrivat')
                  "
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-telefonnummerprivat-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_telefongesch"
                  density="compact"
                  :label="
                    t(
                      'components.konflikteLoesenForm.form.telefonnummerDienstlich'
                    )
                  "
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-telefonnummerdienstlich-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_telefonmobil"
                  density="compact"
                  :label="
                    t('components.konflikteLoesenForm.form.telefonnummerMobil')
                  "
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-telefonnummermobil-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_mailadresse"
                  density="compact"
                  :label="t('components.konflikteLoesenForm.form.mailAdresse')"
                  persistent-placeholder
                  type="email"
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-mailadresse-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_ausgeuebteehrenaemter"
                  density="compact"
                  :label="
                    t(
                      'components.konflikteLoesenForm.form.ausgeuebteEhrenaemter'
                    )
                  "
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-ausgeuebteehrenaemter-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_onlinebewerbung"
                  density="compact"
                  :label="
                    t('components.konflikteLoesenForm.form.onlineBewerbung')
                  "
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-onlinebewerbung-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_neuervorschlag"
                  density="compact"
                  :label="
                    t('components.konflikteLoesenForm.form.neuerVorschlag')
                  "
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-neuervorschlag-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-checkbox
                  v-model="konfliktloesenformdata.person_warbereitstaetigals"
                  density="compact"
                  :label="
                    t(
                      'components.konflikteLoesenForm.form.warBereitsAlsVerwaltungsrichterTaetig',
                      {
                        artEhrenamtJustiz: formattedEhrenamtjustizart(
                          t,
                          useGlobalSettingsStore().getKonfiguration
                            ?.ehrenamtjustizart,
                          1
                        ),
                      }
                    )
                  "
                  :hide-details="true"
                  data-test="konflikteloesenform-warbereitsalsverwaltungsrichtertaetig-checkbox"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-checkbox
                  v-model="
                    konfliktloesenformdata.person_warbereitstaetigalsvorvorperiode
                  "
                  density="compact"
                  :label="
                    t(
                      'components.konflikteLoesenForm.form.warBereitsInVorperiodeAlsVerwaltungsrichterTaetig',
                      {
                        artEhrenamtJustiz: formattedEhrenamtjustizart(
                          t,
                          useGlobalSettingsStore().getKonfiguration
                            ?.ehrenamtjustizart,
                          1
                        ),
                      }
                    )
                  "
                  :hide-details="true"
                  data-test="konflikteloesenform-warbereitsinvorperiodealsverwaltungsrichtertaetig-checkbox"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-text-field
                  v-model="konfliktloesenformdata.person_bewerbungvom"
                  density="compact"
                  :label="t('components.konflikteLoesenForm.form.bewerbungVom')"
                  persistent-placeholder
                  type="date"
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-bewerbungvom-textfield"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col
                class="col"
                cols="5"
              >
                <v-select
                  v-model="konfliktloesenformdata.person_status"
                  :items="BewerbungForm.statuswerte"
                  density="compact"
                  :label="t('components.konflikteLoesenForm.form.status')"
                  persistent-placeholder
                  variant="outlined"
                  :hide-details="true"
                  data-test="konflikteloesenform-status-select"
                />
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-tabs-window-item>
      <v-tabs-window-item
        style="padding: 1em"
        value="datenAusEWOUebernehmen"
        eager
      >
        <v-card>
          <v-card-text>
            <v-row>
              <v-col
                class="col"
                cols="6"
              >
                <span class="text-title-large"
                  >{{
                    t("components.konflikteLoesenForm.header.aktuelleDaten")
                  }}
                </span>
              </v-col>

              <v-col
                class="col"
                cols="5"
              >
                <span class="text-title-large"
                  >{{
                    t("components.konflikteLoesenForm.header.neueDatenAusEWO")
                  }}
                </span>
              </v-col>
            </v-row>
            <v-expansion-panels
              v-model="activeExpansionPanel"
              multiple
              data-test="konflikteloesenform-expansionpanels"
            >
              <v-expansion-panel
                :title="t('components.konflikteLoesenForm.expansionPanel.name')"
                data-test="konflikteloesenform-name-expansionpanel"
              >
                <v-expansion-panel-text>
                  <!-- As the label of the first field is only partially displayed -->
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_familienname"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.familienname')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-familienname-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.familienname'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(FAMILIENNAME)"
                              :disabled="!isKonflikt(FAMILIENNAME)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-familienname-button"
                              @click="konfliktLoesenFamilienname()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(FAMILIENNAME)"
                        v-model="konfliktloesenformdata.ewo_familienname"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.familienname')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewofamilienname-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_geburtsname"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geburtsname')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-persongeburtsname-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.geburtsname'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(GEBURTSNAME)"
                              :disabled="!isKonflikt(GEBURTSNAME)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-persongeburtsname-button"
                              @click="konfliktLoesenGeburtsname()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(GEBURTSNAME)"
                        v-model="konfliktloesenformdata.ewo_geburtsname"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geburtsname')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewopersongeburtsname-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_vorname"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.vorname')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-personvorname-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.vorname'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(VORNAME)"
                              :disabled="!isKonflikt(VORNAME)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-personvorname-button"
                              @click="konfliktLoesenVorname()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(VORNAME)"
                        v-model="konfliktloesenformdata.ewo_vorname"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.vorname')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewopersonvorname-textfield"
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>
              <v-expansion-panel
                :title="
                  t(
                    'components.konflikteLoesenForm.expansionPanel.personenDaten'
                  )
                "
                data-test="konflikteloesenform-personendaten-expansionpanel"
              >
                <v-expansion-panel-text>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_geburtsdatum"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geburtsdatum')
                        "
                        persistent-placeholder
                        type="date"
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-persongeburtsdatum-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.geburtsdatum'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(GEBURTSDATUM)"
                              :disabled="!isKonflikt(GEBURTSDATUM)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-persongeburtsdatum-button"
                              @click="konfliktLoesenGeburtsdatum()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(GEBURTSDATUM)"
                        v-model="konfliktloesenformdata.ewo_geburtsdatum"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geburtsdatum')
                        "
                        persistent-placeholder
                        type="date"
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewopersongeburtsdatum-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-select
                        v-model="konfliktloesenformdata.person_geschlecht"
                        :items="BewerbungForm.geschlechtswerte"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geschlecht')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-persongeschlecht-select"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.geschlecht'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(GESCHLECHT)"
                              :disabled="!isKonflikt(GESCHLECHT)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-persongeschlecht-button"
                              @click="konfliktLoesenGeschlecht()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-select
                        v-if="isKonfliktOriginal(GESCHLECHT)"
                        v-model="konfliktloesenformdata.ewo_geschlecht"
                        :items="BewerbungForm.geschlechtswerte"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geschlecht')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewopersongeschlecht-select"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_ewoid"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.ordnungsmerkmal'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ordnungsmerkmal-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.ordnungsmerkmal'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(ORDNUNGSMERKMAL)"
                              :disabled="!isKonflikt(ORDNUNGSMERKMAL)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-ordnungsmerkmal-button"
                              @click="konfliktLoesenOrdnungsmerkmal()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(ORDNUNGSMERKMAL)"
                        v-model="konfliktloesenformdata.ewo_ewoid"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.ordnungsmerkmal'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewoordnungsmerkmal-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_akademischergrad"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.akademischerGrad'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-akademischergrad-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.akademischerGrad'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(AKADEMISCHERGRAD)"
                              :disabled="!isKonflikt(AKADEMISCHERGRAD)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-akademischergrad-button"
                              @click="konfliktLoesenAkademischergrad()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(AKADEMISCHERGRAD)"
                        v-model="konfliktloesenformdata.ewo_akademischergrad"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.akademischerGrad'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewoakademischergrad-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_geburtsort"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geburtsOrt')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-geburtsort-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.geburtsOrt'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(GEBURTSORT)"
                              :disabled="!isKonflikt(GEBURTSORT)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-geburtsort-button"
                              @click="konfliktLoesenGeburtsort()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(GEBURTSORT)"
                        v-model="konfliktloesenformdata.ewo_geburtsort"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geburtsOrt')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-geburtsort-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_geburtsland"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geburtsLand')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-geburtsland-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.geburtsLand'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(GEBURTSLAND)"
                              :disabled="!isKonflikt(GEBURTSLAND)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-geburtsland-button"
                              @click="konfliktLoesenGeburtsland()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(GEBURTSLAND)"
                        v-model="konfliktloesenformdata.ewo_geburtsland"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.geburtsLand')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewogeburtsland-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_familienstand"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.familienStand')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-familienstand-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.familienStand'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(FAMILIENSTAND)"
                              :disabled="!isKonflikt(FAMILIENSTAND)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-familienstand-button"
                              @click="konfliktLoesenFamilienstand()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(FAMILIENSTAND)"
                        v-model="konfliktloesenformdata.ewo_familienstand"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.familienStand')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewofamilienstand-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="
                          konfliktloesenformdata.person_staatsangehoerigkeit
                        "
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.staatsangehoerigkeit'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-staatsangehoerigkeit-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.staatsangehoerigkeit'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(STAATSANGEHOERIGKEIT)"
                              :disabled="!isKonflikt(STAATSANGEHOERIGKEIT)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-staatsangehoerigkeit-button"
                              @click="konfliktLoesenStaatsangehoerigkeiten()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(STAATSANGEHOERIGKEIT)"
                        v-model="
                          konfliktloesenformdata.ewo_staatsangehoerigkeit
                        "
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.staatsangehoerigkeit'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewostaatsangehoerigkeit-textfield"
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>
              <v-expansion-panel
                :title="
                  t('components.konflikteLoesenForm.expansionPanel.adresse')
                "
                data-test="konflikteloesenform-adresse-expansionpanel"
              >
                <v-expansion-panel-text>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_wohnungsgeber"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.wohnungsgeber')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-wohnungsgeber-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.wohnungsgeber'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(WOHNUNGSGEBER)"
                              :disabled="!isKonflikt(WOHNUNGSGEBER)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-wohnungsgeber-button"
                              @click="konfliktLoesenWohnungsgeber()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(WOHNUNGSGEBER)"
                        v-model="konfliktloesenformdata.ewo_wohnungsgeber"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.wohnungsgeber')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewowohnungsgeber-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_strasse"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.strasse')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-strasse-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.strasse'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(STRASSE)"
                              :disabled="!isKonflikt(STRASSE)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-strasse-button"
                              @click="konfliktLoesenStrasse()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(STRASSE)"
                        v-model="konfliktloesenformdata.ewo_strasse"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.strasse')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewostrasse-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_hausnummer"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.hausnummer')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-hausnummer-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.hausnummer'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(HAUSNUMMER)"
                              :disabled="!isKonflikt(HAUSNUMMER)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-hausnummer-button"
                              @click="konfliktLoesenHausnummer()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(HAUSNUMMER)"
                        v-model="konfliktloesenformdata.ewo_hausnummer"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.hausnummer')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewohausnummer-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_appartmentnummer"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.appartmentNummer'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-appartmentnummer-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.appartmentNummer'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(APPARTMENTNUMMER)"
                              :disabled="!isKonflikt(APPARTMENTNUMMER)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-appartmentnummer-button"
                              @click="konfliktLoesenAppartmentnummer()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(APPARTMENTNUMMER)"
                        v-model="konfliktloesenformdata.ewo_appartmentnummer"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.appartmentNummer'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewoappartmentnummer-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="
                          konfliktloesenformdata.person_buchstabehausnummer
                        "
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.buchstabeHausnummer'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-buchstabehausnummer-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.buchstabeHausnummer'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(BUCHSTABEHAUSNUMMER)"
                              :disabled="!isKonflikt(BUCHSTABEHAUSNUMMER)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-buchstabehausnummer-button"
                              @click="konfliktLoesenBuchstabehausnummer()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(BUCHSTABEHAUSNUMMER)"
                        v-model="konfliktloesenformdata.ewo_buchstabehausnummer"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.buchstabeHausnummer'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewobuchstabehausnummer-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_stockwerk"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.stockwerk')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-stockwerk-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.stockwerk'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(STOCKWERK)"
                              :disabled="!isKonflikt(STOCKWERK)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-stockwerk-button"
                              @click="konfliktLoesenStockwerk()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(STOCKWERK)"
                        v-model="konfliktloesenformdata.ewo_stockwerk"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.stockwerk')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewostockwerk-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="
                          konfliktloesenformdata.person_teilnummerhausnummer
                        "
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.teilnummerHausnummer'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-teilnummerhausnummer-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.teilnummerHausnummer'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(TEILNUMMERHAUSNUMMER)"
                              :disabled="!isKonflikt(TEILNUMMERHAUSNUMMER)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-teilnummerhausnummer-button"
                              @click="konfliktLoesenTeilnummerhausnummer()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(TEILNUMMERHAUSNUMMER)"
                        v-model="
                          konfliktloesenformdata.ewo_teilnummerhausnummer
                        "
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.teilnummerHausnummer'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewoteilnummerhausnummer-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_adresszusatz"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.adressZusatz')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-adresszusatz-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.adressZusatz'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(ADRESSZUSATZ)"
                              :disabled="!isKonflikt(ADRESSZUSATZ)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-adresszusatz-button"
                              @click="konfliktLoesenAdresszusatz()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(ADRESSZUSATZ)"
                        v-model="konfliktloesenformdata.ewo_adresszusatz"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.adressZusatz')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewoadresszusatz-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_postleitzahl"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.postleitzahl')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-postleitzahl-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.postleitzahl'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(POSTLEITZAHL)"
                              :disabled="!isKonflikt(POSTLEITZAHL)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-postleitzahl-button"
                              @click="konfliktLoesenPostleitzahl()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(POSTLEITZAHL)"
                        v-model="konfliktloesenformdata.ewo_postleitzahl"
                        density="compact"
                        :label="
                          t('components.konflikteLoesenForm.form.postleitzahl')
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewopostleitzahl-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_ort"
                        density="compact"
                        :label="t('components.konflikteLoesenForm.form.ort')"
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ort-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.ort'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(ORT)"
                              :disabled="!isKonflikt(ORT)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-ort-button"
                              @click="konfliktLoesenOrt()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(ORT)"
                        v-model="konfliktloesenformdata.ewo_ort"
                        density="compact"
                        :label="t('components.konflikteLoesenForm.form.ort')"
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewoort-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-select
                        v-model="konfliktloesenformdata.person_wohnungsstatus"
                        :items="WOHNUNGSSTATUS_ARTEN"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.wohnungsstatus'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-wohnungsstatus-select"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.wohnungsstatus'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(WOHNUNGSSTATUS)"
                              :disabled="!isKonflikt(WOHNUNGSSTATUS)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-wohnungsstatus-button"
                              @click="konfliktLoesenWohnungsstatus()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-select
                        v-if="isKonfliktOriginal(WOHNUNGSSTATUS)"
                        v-model="konfliktloesenformdata.ewo_wohnungsstatus"
                        :items="WOHNUNGSSTATUS_ARTEN"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.wohnungsstatus'
                          )
                        "
                        persistent-placeholder
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewowohnungsstatus-select"
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>
              <v-expansion-panel
                :title="
                  t('components.konflikteLoesenForm.expansionPanel.sonstiges')
                "
                data-test="konflikteloesenform-sonstiges-expansionpanel"
              >
                <v-expansion-panel-text>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-model="konfliktloesenformdata.person_inmuenchenseit"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.inMuenchenWohnhaftSeit'
                          )
                        "
                        persistent-placeholder
                        type="date"
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-inmuenchenwohnhaftseit-textfield"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.inMuenchenWohnhaftSeit'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(INMUENCHENSEIT)"
                              :disabled="!isKonflikt(INMUENCHENSEIT)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-inmuenchenwohnhaftseit-button"
                              @click="konfliktLoesenInMuenchenseit()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-text-field
                        v-if="isKonfliktOriginal(INMUENCHENSEIT)"
                        v-model="konfliktloesenformdata.ewo_inmuenchenseit"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.inMuenchenWohnhaftSeit'
                          )
                        "
                        persistent-placeholder
                        type="date"
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewoinmuenchenwohnhaftseit-textfield"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-textarea
                        v-model="konfliktloesenformdata.person_auskunftssperre"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.auskunftsSperre'
                          )
                        "
                        persistent-placeholder
                        no-resize
                        rows="2"
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-auskunftssperre-textarea"
                      />
                    </v-col>
                    <v-col
                      class="col"
                      cols="1"
                    >
                      <v-tooltip
                        :text="
                          t(
                            'components.konflikteLoesenForm.buttons.datenAusEWOUebernehmen',
                            {
                              feldname: t(
                                'components.konflikteLoesenForm.form.auskunftsSperre'
                              ),
                            }
                          )
                        "
                      >
                        <template #activator="{ props }">
                          <span v-bind="props">
                            <v-btn
                              v-if="isKonfliktOriginal(AUSKUNFTSSPERRE)"
                              :disabled="!isKonflikt(AUSKUNFTSSPERRE)"
                              class="button-konflikt-loesen"
                              data-test="konflikteloesenform-auskunftssperre-button"
                              @click="konfliktLoesenAuskunftssperren()"
                            >
                              <v-icon :icon="mdiTransferLeft" />
                            </v-btn>
                          </span>
                        </template>
                      </v-tooltip>
                    </v-col>
                    <v-col
                      class="col"
                      cols="5"
                    >
                      <v-textarea
                        v-if="isKonfliktOriginal(AUSKUNFTSSPERRE)"
                        v-model="konfliktloesenformdata.ewo_auskunftssperre"
                        density="compact"
                        :label="
                          t(
                            'components.konflikteLoesenForm.form.auskunftsSperre'
                          )
                        "
                        persistent-placeholder
                        no-resize
                        rows="2"
                        variant="outlined"
                        :hide-details="true"
                        data-test="konflikteloesenform-ewoauskunftssperre-textarea"
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-card-text>
        </v-card>
      </v-tabs-window-item>
    </v-tabs-window>
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
  VCard,
  VCardText,
  VCheckbox,
  VCol,
  VExpansionPanel,
  VExpansionPanels,
  VExpansionPanelText,
  VForm,
  VIcon,
  VRow,
  VSelect,
  VTab,
  VTabs,
  VTabsWindow,
  VTabsWindowItem,
  VTextarea,
  VTextField,
  VTooltip,
} from "vuetify/components";

import BewerbungForm from "@/components/bewerbungen/BewerbungForm.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { STATUS_INDICATORS, WOHNUNGSSTATUS_ARTEN } from "@/Constants";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";
import { formattedEhrenamtjustizart } from "@/tools/Helper";

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

const activeExpansionPanel = ref<number[]>([]);

const active_tab = ref("datenAusEWOUebernehmen");

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
    snackbarStore.push({
      color: STATUS_INDICATORS.WARNING,
      text: t("components.konflikteLoesenForm.form.messages.keineEWODaten"),
    });
  }

  if (isKonflikts([FAMILIENNAME, GEBURTSNAME, VORNAME])) {
    // Namen
    activeExpansionPanel.value.push(0);
  }
  if (
    isKonflikts([
      GEBURTSDATUM,
      GESCHLECHT,
      ORDNUNGSMERKMAL,
      AKADEMISCHERGRAD,
      GEBURTSORT,
      GEBURTSLAND,
      FAMILIENSTAND,
      STAATSANGEHOERIGKEIT,
    ])
  ) {
    // Personendaten
    activeExpansionPanel.value.push(1);
  }
  if (
    isKonflikts([
      WOHNUNGSGEBER,
      STRASSE,
      HAUSNUMMER,
      APPARTMENTNUMMER,
      BUCHSTABEHAUSNUMMER,
      STOCKWERK,
      TEILNUMMERHAUSNUMMER,
      ADRESSZUSATZ,
      POSTLEITZAHL,
      ORT,
      WOHNUNGSSTATUS,
    ])
  ) {
    // Adresse
    activeExpansionPanel.value.push(2);
  }
  if (isKonflikts([INMUENCHENSEIT, AUSKUNFTSSPERRE])) {
    // Sonstiges
    activeExpansionPanel.value.push(3);
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
      snackbarStore.push({
        color: STATUS_INDICATORS.WARNING,
        text: t(
          "components.konflikteLoesenForm.form.messages.fehlerhafteEingabe"
        ),
      });
    } else {
      emits("save");
    }
  });
}

function isKonflikts(konfliktFelder: string[]): boolean {
  return konfliktFelder.some((konfliktFeld) => isKonflikt(konfliktFeld));
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
</style>
