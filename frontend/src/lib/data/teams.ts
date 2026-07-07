import type { Team } from "../types";

function team(id: string, code: string, name: string, league: Team["league"]): Team {
  return { id, code, name, league };
}

export const TEAMS: Record<string, Team> = {
  // Premier League
  ars: team("ars", "ARS", "Arsenal", "EPL"),
  mci: team("mci", "MCI", "Man City", "EPL"),
  liv: team("liv", "LIV", "Liverpool", "EPL"),
  che: team("che", "CHE", "Chelsea", "EPL"),
  tot: team("tot", "TOT", "Tottenham", "EPL"),
  mun: team("mun", "MUN", "Man United", "EPL"),
  new: team("new", "NEW", "Newcastle", "EPL"),
  avl: team("avl", "AVL", "Aston Villa", "EPL"),
  bha: team("bha", "BHA", "Brighton", "EPL"),
  whu: team("whu", "WHU", "West Ham", "EPL"),
  cry: team("cry", "CRY", "Crystal Palace", "EPL"),
  wol: team("wol", "WOL", "Wolves", "EPL"),
  ful: team("ful", "FUL", "Fulham", "EPL"),
  eve: team("eve", "EVE", "Everton", "EPL"),
  bre: team("bre", "BRE", "Brentford", "EPL"),
  nfo: team("nfo", "NFO", "Nott'm Forest", "EPL"),
  bou: team("bou", "BOU", "Bournemouth", "EPL"),
  lei: team("lei", "LEI", "Leicester", "EPL"),
  ips: team("ips", "IPS", "Ipswich", "EPL"),
  sou: team("sou", "SOU", "Southampton", "EPL"),

  // La Liga
  rma: team("rma", "RMA", "Real Madrid", "LALIGA"),
  bar: team("bar", "BAR", "Barcelona", "LALIGA"),
  atm: team("atm", "ATM", "Atletico Madrid", "LALIGA"),
  rso: team("rso", "RSO", "Real Sociedad", "LALIGA"),
  gir: team("gir", "GIR", "Girona", "LALIGA"),
  ath: team("ath", "ATH", "Athletic Bilbao", "LALIGA"),
  vil: team("vil", "VIL", "Villarreal", "LALIGA"),
  bet: team("bet", "BET", "Real Betis", "LALIGA"),
  sev: team("sev", "SEV", "Sevilla", "LALIGA"),
  val: team("val", "VAL", "Valencia", "LALIGA"),
  osa: team("osa", "OSA", "Osasuna", "LALIGA"),
  cel: team("cel", "CEL", "Celta Vigo", "LALIGA"),
  ray: team("ray", "RAY", "Rayo Vallecano", "LALIGA"),
  mal: team("mal", "MAL", "Mallorca", "LALIGA"),
  ala: team("ala", "ALA", "Alaves", "LALIGA"),
  get: team("get", "GET", "Getafe", "LALIGA"),
  leg: team("leg", "LEG", "Leganes", "LALIGA"),
  lpa: team("lpa", "LPA", "Las Palmas", "LALIGA"),
  vld: team("vld", "VLD", "Valladolid", "LALIGA"),
  esp: team("esp", "ESP", "Espanyol", "LALIGA"),

  // Bundesliga
  bay: team("bay", "BAY", "Bayern Munich", "BUNDESLIGA"),
  bvb: team("bvb", "BVB", "Dortmund", "BUNDESLIGA"),
  rbl: team("rbl", "RBL", "RB Leipzig", "BUNDESLIGA"),
  b04: team("b04", "B04", "Leverkusen", "BUNDESLIGA"),
  sge: team("sge", "SGE", "Eintracht Frankfurt", "BUNDESLIGA"),
  vfb: team("vfb", "VFB", "Stuttgart", "BUNDESLIGA"),
  bmg: team("bmg", "BMG", "M'gladbach", "BUNDESLIGA"),
  wob: team("wob", "WOB", "Wolfsburg", "BUNDESLIGA"),
  svw: team("svw", "SVW", "Werder Bremen", "BUNDESLIGA"),
  fcu: team("fcu", "FCU", "Union Berlin", "BUNDESLIGA"),
  tsg: team("tsg", "TSG", "Hoffenheim", "BUNDESLIGA"),
  fca: team("fca", "FCA", "Augsburg", "BUNDESLIGA"),
  scf: team("scf", "SCF", "Freiburg", "BUNDESLIGA"),
  m05: team("m05", "M05", "Mainz 05", "BUNDESLIGA"),
  koe: team("koe", "KOE", "Koln", "BUNDESLIGA"),
  boc: team("boc", "BOC", "Bochum", "BUNDESLIGA"),
  kie: team("kie", "KIE", "Holstein Kiel", "BUNDESLIGA"),
  stp: team("stp", "STP", "St. Pauli", "BUNDESLIGA"),

  // Serie A
  int: team("int", "INT", "Inter Milan", "SERIEA"),
  juv: team("juv", "JUV", "Juventus", "SERIEA"),
  mil: team("mil", "MIL", "AC Milan", "SERIEA"),
  nap: team("nap", "NAP", "Napoli", "SERIEA"),
  ata: team("ata", "ATA", "Atalanta", "SERIEA"),
  rom: team("rom", "ROM", "Roma", "SERIEA"),
  laz: team("laz", "LAZ", "Lazio", "SERIEA"),
  fio: team("fio", "FIO", "Fiorentina", "SERIEA"),
  bol: team("bol", "BOL", "Bologna", "SERIEA"),
  tor: team("tor", "TOR", "Torino", "SERIEA"),
  udi: team("udi", "UDI", "Udinese", "SERIEA"),
  gen: team("gen", "GEN", "Genoa", "SERIEA"),
  ver: team("ver", "VER", "Hellas Verona", "SERIEA"),
  lec: team("lec", "LEC", "Lecce", "SERIEA"),
  cag: team("cag", "CAG", "Cagliari", "SERIEA"),
  emp: team("emp", "EMP", "Empoli", "SERIEA"),
  par: team("par", "PAR", "Parma", "SERIEA"),
  com: team("com", "COM", "Como", "SERIEA"),
  ven: team("ven", "VEN", "Venezia", "SERIEA"),
  mon2: team("mon2", "MNZ", "Monza", "SERIEA"),

  // Ligue 1
  psg: team("psg", "PSG", "Paris SG", "LIGUE1"),
  mar: team("mar", "MAR", "Marseille", "LIGUE1"),
  mon: team("mon", "MON", "Monaco", "LIGUE1"),
  lyo: team("lyo", "LYO", "Lyon", "LIGUE1"),
  lil: team("lil", "LIL", "Lille", "LIGUE1"),
  nic: team("nic", "NIC", "Nice", "LIGUE1"),
  lens: team("lens", "LEN", "Lens", "LIGUE1"),
  ren: team("ren", "REN", "Rennes", "LIGUE1"),
  nan: team("nan", "NAN", "Nantes", "LIGUE1"),
  str: team("str", "STR", "Strasbourg", "LIGUE1"),
  tou: team("tou", "TOU", "Toulouse", "LIGUE1"),
  bre2: team("bre2", "BST", "Brest", "LIGUE1"),
  rei: team("rei", "REI", "Reims", "LIGUE1"),
  mtp: team("mtp", "MTP", "Montpellier", "LIGUE1"),
  ang: team("ang", "ANG", "Angers", "LIGUE1"),
  aux: team("aux", "AUX", "Auxerre", "LIGUE1"),
  hav: team("hav", "HAV", "Le Havre", "LIGUE1"),
  sai: team("sai", "SAI", "Saint-Etienne", "LIGUE1"),
};

export function getTeam(id: string): Team {
  const t = TEAMS[id];
  if (!t) throw new Error(`Unknown team id: ${id}`);
  return t;
}
