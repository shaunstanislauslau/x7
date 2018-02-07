package x7.core.bean;

public enum Predicate implements SqlScript{

	EQ {
		@Override
		public String sql() {
			return " = ";
		}
	},
	LT {
		@Override
		public String sql() {
			return " < ";
		}
	},
	GT {
		@Override
		public String sql() {
			return " > ";
		}
	},
	LTE {
		@Override
		public String sql() {
			return " <= ";
		}
	},
	GTE {
		@Override
		public String sql() {
			return " >= ";
		}
	},
	NOT {
		@Override
		public String sql() {
			return " <> ";
		}
	},
	LIKE {
		@Override
		public String sql() {
			return " like ";
		}
	},
	IN {
		@Override
		public String sql() {
			return " in ";
		}
	},
	NOT_IN {
		@Override
		public String sql() {
			return " not in ";
		}
	},
	IS_NOT_NULL {
		@Override
		public String sql() {
			return " is not null ";
		}
	},
	IS_NULL {
		@Override
		public String sql() {
			return " is null ";
		}
	},
	BETWEEN {
		@Override
		public String sql() {
			return " between ";
		}
	},
	X {
		@Override
		public String sql() {
			return "x";
		}
	},;
	
}
