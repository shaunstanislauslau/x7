package x7.core.bean;

public enum Conjunction implements SqlScript{

	AND{
		@Override
		public String sql() {
			return " and ";
		}
	},
	OR{
		@Override
		public String sql() {
			return " or ";
		}
	},
	ORDER_BY{

		@Override
		public String sql() {
			return " order by ";
		}
		
	},
	GROUP_BY{

		@Override
		public String sql() {
			return " group by ";
		}
		
	},
	HAVING{

		@Override
		public String sql() {
			return " having ";
		}
		
	},
	;
}
